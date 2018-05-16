package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.features.kotlin.*
import net.minecraft.util.math.Vec3d
import sun.jvm.hotspot.debugger.x86.X86ThreadContext.R1
import com.ibm.icu.impl.Row.R2
import org.lwjgl.util.vector.Vector3f.cross

data class Ray(var origin: Vec3d, var normal: Vec3d) {

    fun pointWithMultiplier(multiplier: Double): Vec3d {
        return origin + normal * multiplier
    }

    fun intersectWithTriangle(v0: Vec3d, v1: Vec3d, v2: Vec3d): Double? {
        val v0v1 = v1 - v0
        val v0v2 = v2 - v0
        val pvec = normal.cross(v0v2)
        val det = v0v1.dot(pvec)

        if (det < 0.000001)
            return null

        val invDet = (1.0 / det).toFloat()
        val tvec = origin - v0
        val u = tvec.dot(pvec) * invDet

        if (u < 0 || u > 1)
            return null

        val qvec = tvec.cross(v0v1)
        val v = normal.dot(qvec) * invDet

        return if (v < 0 || u + v > 1) null else v0v2.dot(qvec) * invDet
    }

    // https://stackoverflow.com/a/21114992/1541907
    fun intersectWithPlane(pointOnPlane: Vec3d, planeNormal: Vec3d): Double? {
        val dR = -normal

        val ndotdR = planeNormal dot dR

        if (Math.abs(ndotdR) < 1e-6f) { // Choose your tolerance
            return null
        }

        val t = -planeNormal dot (origin- pointOnPlane) / ndotdR
        return -t
    }

    /**
     * Returns the multiplier of this normal that gets the closest point on this ray, with the second component being
     * the same for the other ray
     */
    @Suppress("LocalVariableName")
    // http://web.archive.org/web/20180516050524/http://morroworks.com/Content/Docs/Rays%20closest%20point.pdf
    fun closestPointTo(other: Ray): Pair<Double, Double> {
        val A = this.origin
        val a_ = this.normal
        val B = other.origin
        val b_ = other.normal

        val c_ = B - A

        val p = a_ dot b_
        val q = a_ dot c_
        val r = b_ dot c_
        val s = a_ dot a_
        val t = b_ dot b_

        assert(s != 0.0)
        assert(t != 0.0)
        assert(p*p != s*t)

        val d = (-p*r + q*t)/
                (s*t - p*p)
        val e = (p*q - r*s)/
                (s*t - p*p)

        return d to e
    }
}

