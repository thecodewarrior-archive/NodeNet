package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.features.kotlin.*
import net.minecraft.util.math.Vec3d
import sun.jvm.hotspot.debugger.x86.X86ThreadContext.R1
import com.ibm.icu.impl.Row.R2
import org.lwjgl.util.vector.Vector3f.cross





data class Ray(val origin: Vec3d, val normal: Vec3d) {
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
}

