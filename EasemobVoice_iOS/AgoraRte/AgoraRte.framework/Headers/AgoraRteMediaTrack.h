//
//  AgoraRte
//
//  Created by Cavan on 2020/11/14.
//

#import <Foundation/Foundation.h>
#import "AgoraRteEnums.h"
#import "AgoraRteObjects.h"

NS_ASSUME_NONNULL_BEGIN

__attribute__((visibility("default")))
@interface AgoraRteMediaTrack : NSObject
@property (readonly, nonatomic, assign) AgoraRteVideoSourceType videoSourceType;
@property (readonly, nonatomic, assign) AgoraRteAudioSourceType audioSourceType;
@property (readonly, nonatomic, assign) AgoraRteMediaTrackState state;

- (AgoraRteError * _Nullable)start;
- (AgoraRteError * _Nullable)stop;
@end

__attribute__((visibility("default")))
@interface AgoraRteCameraVideoTrack : AgoraRteMediaTrack
@property (readonly, nonatomic, assign) AgoraRteCameraSource cameraSource;

- (AgoraRteError * _Nullable)setCameraSource:(AgoraRteCameraSource)source;
- (AgoraRteError * _Nullable)switchCamera;

- (AgoraRteError * _Nullable)setView:(VIEW_CLASS *)view;
- (AgoraRteError * _Nullable)setRenderConfig:(AgoraRteRenderConfig *)config;
- (AgoraRteError * _Nullable)setVideoEncoderConfig:(AgoraRteVideoEncoderConfig *)config;

#pragma mark Unavailable Initializers
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
@end

__attribute__((visibility("default")))
@interface AgoraRteMicrophoneAudioTrack : AgoraRteMediaTrack
@property (readonly, nonatomic, assign) BOOL isEnableLocalPlayback;

- (AgoraRteError * _Nullable)enableLocalPlayback;
- (AgoraRteError * _Nullable)disableLocalPlayback;
- (AgoraRteError * _Nullable)setAudioEncoderConfig:(AgoraRteAudioEncoderConfig *)config;

#pragma mark Unavailable Initializers
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
@end


@class AgoraRteMediaPlayer;
@protocol AgoraRteMediaPlayerDelegate <NSObject>
@optional
- (void)track:(AgoraRteMediaPlayer *)track
didChangePlayerState:(AgoraRteMediaPlayerState)state;

- (void)track:(AgoraRteMediaPlayer *)track
didOccurError:(AgoraRteError *)error;
@end

__attribute__((visibility("default")))
@interface AgoraRteMediaPlayer : NSObject
@property (readonly, nonatomic, assign) NSUInteger duration;
@property (readonly, nonatomic, assign) NSUInteger playPosition;
@property (readonly, nonatomic, assign) AgoraRteMediaPlayerState state;
@property (nonatomic, weak) id<AgoraRteMediaPlayerDelegate> delegate;

- (AgoraRteError * _Nullable)openWithURL:(NSString *)url;
- (AgoraRteError * _Nullable)setupLoopCount:(NSUInteger)count;
- (AgoraRteError * _Nullable)play;
- (AgoraRteError * _Nullable)pause;
- (AgoraRteError * _Nullable)stop;
- (AgoraRteError * _Nullable)resume;
- (AgoraRteError * _Nullable)seekWithPosition:(NSUInteger)position;
- (AgoraRteError * _Nullable)adjustPlayoutVolume:(NSUInteger)volume;
- (AgoraRteError * _Nullable)adjustPublishVolume:(NSUInteger)volume;

#pragma mark Unavailable Initializers
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
@end

NS_ASSUME_NONNULL_END
