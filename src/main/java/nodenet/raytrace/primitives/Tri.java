package nodenet.raytrace.primitives;

import net.minecraft.util.math.Vec3d;

import nodenet.Matrix4;
import nodenet.raytrace.RayTraceUtil;
import nodenet.raytrace.RayTraceUtil.IRenderableTraceResult;
import nodenet.raytrace.RayTraceUtil.SimpleRenderableTraceResult;
import nodenet.raytrace.RayTraceUtil.TraceablePrimitive;
import nodenet.raytrace.RayTraceUtil.VertexList;
import nodenet.util.GeneralUtil;
import scala.actors.threadpool.Arrays;

public class Tri extends TraceablePrimitive<Tri> {

	private static final double SMALL_NUM = 0.00000001f;
	
	Vec3d v1, v2, v3;
	
	public Tri(Vec3d v1, Vec3d v2, Vec3d v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	
	@Override
	public Vec3d[] edges() {
		return new Vec3d[] { v1, v2, v2, v3, v3, v1 };
	}
	
	@Override
	public Vec3d[] points() {
		return new Vec3d[] { v1, v2, v3 };
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IRenderableTraceResult<Tri> trace(Vec3d startRaw, Vec3d endRaw) {
		Vec3d v1 = this.v1;
		Vec3d v2 = this.v2;
		Vec3d v3 = this.v3;
		Vec3d start = startRaw, end = endRaw;
		
		Vec3d intersect;
		Vec3d    u, v, n;              // triangle vectors
	    Vec3d    dir, w0, w;           // ray vectors
	    double     r, a, b;              // params to calc ray-plane intersect

	    // get triangle edge vectors and plane normal
	    u = v2.subtract(v1);
	    v = v3.subtract(v1);
	    n = u.crossProduct(v);              // cross product
	    if (n.equals( Vec3d.ZERO ))             // triangle is degenerate
	        return RayTraceUtil.miss(this);                   // do not deal with this case

	    dir = end.subtract(start);             // ray direction vector
	    start = start.subtract(dir.normalize().scale(0.25));
	    dir = end.subtract(start);             // ray direction vector
	    w0 = start.subtract(v1);
	    a = -n.dotProduct(w0);
	    b =  n.dotProduct(dir);
	    if (Math.abs(b) < SMALL_NUM) {     // ray is  parallel to triangle plane
	        if (a == 0)                 // ray lies in triangle plane
	            return RayTraceUtil.miss(this);
	        else
	        	return RayTraceUtil.miss(this);              // ray disjoint from plane
	    }

	    // get intersect point of ray with triangle plane
	    r = a / b;
	    if (r < 0.0)                    // ray goes away from triangle
	        return RayTraceUtil.miss(this);                   // => no intersect
	    if (r > 1.0)                    // ray doesn't reach triangle
	    	return RayTraceUtil.miss(this);                   // => no intersect

	    intersect = start.add(dir.scale(r));            // intersect point of ray and plane
	    
	    float angles = 0;

        v1 = new Vec3d(intersect.xCoord - this.v1.xCoord, intersect.yCoord - this.v1.yCoord, intersect.zCoord - this.v1.zCoord).normalize();
        v2 = new Vec3d(intersect.xCoord - this.v2.xCoord, intersect.yCoord - this.v2.yCoord, intersect.zCoord - this.v2.zCoord).normalize();
        v3 = new Vec3d(intersect.xCoord - this.v3.xCoord, intersect.yCoord - this.v3.yCoord, intersect.zCoord - this.v3.zCoord).normalize();

        angles += Math.acos(v1.dotProduct(v2));
        angles += Math.acos(v2.dotProduct(v3));
        angles += Math.acos(v3.dotProduct(v1));

        if(Math.abs(angles - 2*Math.PI) > 0.005)
        	return RayTraceUtil.miss(this);
                
        return new SimpleRenderableTraceResult<Tri>(startRaw, intersect, this,
        		Arrays.asList(new VertexList[] {
        			new VertexList(false, new Vec3d[] { this.v1, this.v2, this.v3 })
        		})
        	);
	}
	
	@Override
	public Tri clone() {
		return new Tri(v1, v2, v3);
	}

	@Override
	public void rotate(int yRotation) {
		v1 = GeneralUtil.rotateVector(yRotation, v1);
		v2 = GeneralUtil.rotateVector(yRotation, v2);
		v3 = GeneralUtil.rotateVector(yRotation, v3);
	}

	@Override
	public void translate(Vec3d vec) {
		v1 = v1.add(vec);
		v2 = v2.add(vec);
		v3 = v3.add(vec);
	}
	
	@Override
	public void apply(Matrix4 matrix) {
		v1 = matrix.apply(v1);
		v2 = matrix.apply(v2);
		v3 = matrix.apply(v3);
	}
	
}
