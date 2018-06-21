#include <jni.h>
#include <android/log.h>
#include <com_yj_professional_utils_JniCall.h>

extern "C"
{
	#include <myfilter.h>
	#include <myfilter_emxAPI.h>
	#include <log_help.h>
}
#define TAG "######"


/*
 * ��ͨ�˲���ʵ�� ����c����
 */
//JNIEXPORT jdoubleArray JNICALL Java_com_yj_professional_activity_DetectionActivity_process_Data
//JNIEXPORT jdoubleArray JNICALL Java_com_yj_professional_activity_DetectionActivity_process_1Data
JNIEXPORT jdoubleArray JNICALL Java_com_yj_professional_utils_JniCall_process_1Data
  (JNIEnv *env, jobject obj, jdoubleArray source){
	jint length = env->GetArrayLength(source);//��ȡ����ĳ���
	jdouble *source_arr = env->GetDoubleArrayElements(source, 0);//��ȡ����ָ��
//	//�½�����len����
	jdoubleArray result = env -> NewDoubleArray(length);
	jdouble *result_arr = env->GetDoubleArrayElements(result, 0);

	//��ͨ�˲���ʵ��--ģ��matlab
	emxArray_real_T *temp = emxCreateWrapper_real_T(result_arr,1,length);
//	emxArray_real_T *temp = emxCreate_real_T(1, length);
	//
	myfilter(emxCreateWrapper_real_T(source_arr,1,length), temp);
	//��2017.11.01 20:19:10  ���ڵ��Գɹ�

	env->SetDoubleArrayRegion(result, 0, length, result_arr);
	env->SetDoubleArrayRegion(source, 0, length, source_arr);
	env->ReleaseDoubleArrayElements(source, source_arr, 0);//�ͷ���Դ  �мǲ�������
	env-> ReleaseDoubleArrayElements(result, result_arr, 0);
//	LOGE("######%d",length);
	return result;
}
