#include <jni.h>
#include <GLES/gl.h>

extern "C" {

GLuint g_textureName;
int g_nX = 0;
int g_nY = 0;
int g_nPandaWidth = 0;
int g_nPandaHeight = 0;

void
Java_thetana_cow_GLView_nativeSetTextureData(JNIEnv *env, jobject thiz, jintArray arr, jint width,
                                             jint height, jint w, jint h, jint x, jint y) {
    jint *data = env->GetIntArrayElements(arr, NULL);
    glDeleteTextures(1, &g_textureName);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    g_nPandaWidth = w;
    g_nPandaHeight = h;
    g_nX = x;
    g_nY = y;
    glGenTextures(1, &g_textureName);
    glBindTexture(GL_TEXTURE_2D, g_textureName);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                 (void *) data);
    env->ReleaseIntArrayElements(arr, data, JNI_ABORT);
    GLfloat vertices[12] = {
            g_nX, g_nY + g_nPandaHeight, 0.0f, // LEFT  | BOTTOM
            g_nX + g_nPandaWidth, g_nY + g_nPandaHeight, 0.0f, // RIGHT | BOTTOM
            g_nX, g_nY, 0.0f, // LEFT  | TOP
            g_nX + g_nPandaWidth, g_nY, 0.0f // RIGHT | TOP
    };
    GLfloat texture[8] = {
            0, 1,
            1, 1,
            0, 0,
            1, 0
    };
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, g_textureName); // ??
    glEnableClientState(GL_VERTEX_ARRAY);
    glVertexPointer(3, GL_FLOAT, 0, vertices);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    glTexCoordPointer(2, GL_FLOAT, 0, texture);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    glDisableClientState(GL_VERTEX_ARRAY);
    glDisable(GL_TEXTURE_2D);
}
void Java_thetana_cow_GLView_nativeCreated(JNIEnv *env) {
    glClearColor(1.0, 1.0, 1.0, 1.0);
}
void Java_thetana_cow_GLView_nativeChanged(JNIEnv *env, jobject thiz, jint w, jint h) {
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrthof(0.0f, w, h, 0.0f, 1.0f, -1.0f);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glViewport(0, 0, w, h);
}
void Java_thetana_cow_GLView_nativeUpdateGame(JNIEnv *env) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}
void Java_thetana_cow_GLView_nativeOnTouchEvent(JNIEnv *env, jobject thiz, jint x, jint y,
                                                jint touchFlag) {
}

}