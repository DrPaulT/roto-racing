package uk.co.drdv.rotoracing;

public class Quaternion {

    public float x;
    public float y;
    public float z;
    public float w;

    // Zero-rotation quaternion.
    public Quaternion() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
    }

    public void setAll(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // Create a rotation matrix from a quaternion.
    public static void toRotationMatrix(Quaternion q, float[] m) {
        m[0] = 1.0f - 2.0f * (q.y * q.y + q.z * q.z);
        m[1] = 2.0f * (q.x * q.y - q.z * q.w);
        m[2] = 2.0f * (q.z * q.x + q.y * q.w);
        m[3] = 0.0f;
        m[4] = 2.0f * (q.x * q.y + q.w * q.z);
        m[5] = 1.0f - 2.0f * (q.z * q.z + q.x * q.x);
        m[6] = 2.0f * (q.y * q.z - q.x * q.w);
        m[7] = 0.0f;
        m[8] = 2.0f * (q.z * q.x - q.y * q.w);
        m[9] = 2.0f * (q.y * q.z + q.x * q.w);
        m[10] = 1.0f - 2.0f * (q.y * q.y + q.x * q.x);
        m[11] = 0.0f;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }
}