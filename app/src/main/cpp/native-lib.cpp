#include <jni.h>
#include <GLES/gl.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

extern "C" {

GLuint g_textureName;
float g_nX = 0;
float g_nY = 0;
float g_nPandaWidth = 0;
float g_nPandaHeight = 0;

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
            g_nX, g_nY + g_nPandaHeight, 0, // LEFT  | BOTTOM
            g_nX + g_nPandaWidth, g_nY + g_nPandaHeight, 0, // RIGHT | BOTTOM
            g_nX, g_nY, 0, // LEFT  | TOP
            g_nX + g_nPandaWidth, g_nY, 0 // RIGHT | TOP
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

JNIEXPORT jlong JNICALL
Java_thetana_cow_GameActivity_loadCascade(JNIEnv *env, jclass type,
                                          jstring cascadeFileName_) {
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    } else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);


    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
}

float resize(Mat img_src, Mat &img_resize, int resize_width) {

    float scale = resize_width / (float) img_src.cols;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    } else {
        img_resize = img_src;
    }
    return scale;
}

JNIEXPORT void JNICALL
Java_thetana_cow_GameActivity_detect(JNIEnv *env, jclass type,
                                     jlong cascadeClassifier_face,
                                     jlong cascadeClassifier_eye,
                                     jlong matAddrInput,
                                     jlong matAddrResult) {
    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale(img_resize, faces, 1.1, 2,
                                                                     0 | CASCADE_SCALE_IMAGE,
                                                                     Size(30, 30));


    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());

    for (int i = 0; i < faces.size(); i++) {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;
        Rect rect(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
        img_result = img_result(rect);
        break;
        Point center(real_facesize_x + real_facesize_width / 2,
                     real_facesize_y + real_facesize_height / 2);
        ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0, 0,
                360,
                Scalar(100, 200, 255), 10, 8, 0);

        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
        Mat faceROI = img_gray(face_area);
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale(faceROI, eyes, 1.1, 2,
                                                                        0 | CASCADE_SCALE_IMAGE,
                                                                        Size(30, 30));

        for (size_t j = 0; j < eyes.size(); j++) {
            Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
                             real_facesize_y + eyes[j].y + eyes[j].height / 2);
            int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
            circle(img_result, eye_center, radius, Scalar(100, 255, 200), 10, 8, 0);
        }
    }
}
}