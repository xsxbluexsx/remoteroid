all : kcm remoteroid_kcm_bin

kcm : ByteOrder.h KeycodeLabels.h kcm.cpp
	g++ ./kcm.cpp -o kcm
	
remoteroid_kcm_bin : remoteroid.kcm kcm
	./kcm remoteroid_below_3.0.kcm remoteroid.kcm.bin

clean :
	rm -rf ./kcm remoteroid.kcm.bin