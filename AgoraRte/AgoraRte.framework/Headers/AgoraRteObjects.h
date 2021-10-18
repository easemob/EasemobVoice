//
//  AgoraRte
//
//  Created by Cavan on 2020/11/14.
//

#import <UIKit/UIKit.h>
#import <AgoraLog/AgoraLogger.h>
#import "AgoraRteEnums.h"

NS_ASSUME_NONNULL_BEGIN

__attribute__((visibility("default")))
@interface AgoraRteEngineConfig : NSObject
@property (nonatomic, copy) NSString *appId;
@property (nonatomic, copy) NSString *customerId;
@property (nonatomic, copy) NSString *customerCertificate;
@property (nonatomic, copy) NSString *userId;
@property (nonatomic, copy) NSString *logFilePath;
@property (nonatomic, assign) AgoraConsolePrintType logConsolePrintType;

- (instancetype)initWithAppId:(NSString *)appId
                   customerId:(NSString *)customerId
          customerCertificate:(NSString *)customerCertificate
                       userId:(NSString *)userId;
@end

__attribute__((visibility("default")))
@interface AgoraRteSceneConfig : NSObject
@property (nonatomic, copy) NSString *sceneId;

- (instancetype)initWithSceneId:(NSString *)sceneId;
@end

__attribute__((visibility("default")))
@interface AgoraRteSceneJoinOptions : NSObject
@property (nonatomic, copy) NSString *userName;
@property (nonatomic, copy) NSString *userRole;
@property (nonatomic, copy) NSString *streamId;

- (instancetype)initWithUserName:(NSString *)userName
                        userRole:(NSString *)userRole;
@end

__attribute__((visibility("default")))
@interface AgoraRteRemoteStreamInfo : NSObject
@property (nonatomic, copy) NSString *streamId;
@property (nonatomic, copy, nullable) NSString *streamName;
@property (nonatomic, copy) NSString *userId;
@property (nonatomic, assign) AgoraRteMediaStreamType mediaStreamType;
@property (nonatomic, assign) AgoraRteVideoSourceType videoSourceType;
@property (nonatomic, assign) AgoraRteAudioSourceType audioSourceType;

- (instancetype)initWithStreamId:(NSString *)streamId
                          userId:(NSString *)userId
                 mediaStreamType:(AgoraRteMediaStreamType)mediaStreamType
                 videoSourceType:(AgoraRteVideoSourceType)videoSourceType
                 audioSourceType:(AgoraRteAudioSourceType)audioSourceType;
@end

__attribute__((visibility("default")))
@interface AgoraRteRenderConfig : NSObject
@property (nonatomic, assign) AgoraRteRenderMode renderMode;
@property (nonatomic, assign) BOOL isMirror;

- (instancetype)initWithRenderMode:(AgoraRteRenderMode)renderMode;
@end

__attribute__((visibility("default")))
@interface AgoraRteVideoEncoderConfig : NSObject
@property (nonatomic, assign) CGSize dimension;
@property (nonatomic, assign) NSUInteger frameRate;
@property (nonatomic, assign) NSUInteger bitrate;
@property (nonatomic, assign) AgoraRteVideoOutputOrientationMode orientationMode;
@property (nonatomic, assign) AgoraRteDegradationPreference degradationPreference;

- (instancetype)initWithDimension:(CGSize)dimension
                        frameRate:(NSUInteger)frameRate
                          bitrate:(NSUInteger)bitrate
                  orientationMode:(AgoraRteVideoOutputOrientationMode)orientationMode
            degradationPreference:(AgoraRteDegradationPreference)degradationPreference;
@end

__attribute__((visibility("default")))
@interface AgoraRteAudioEncoderConfig : NSObject
@property (nonatomic, assign) AgoraRteAudioProfile profile;
@property (nonatomic, assign) AgoraRteAudioScenario scenario;

- (instancetype)initWithProfile:(AgoraRteAudioProfile)profile
                       scenario:(AgoraRteAudioScenario)scenario;
@end

#pragma mark - readonly
__attribute__((visibility("default")))
@interface AgoraRteError : NSObject
@property (readonly, nonatomic, assign) NSInteger code;
@property (readonly, nonatomic, copy, nullable) NSString *message;
@end

__attribute__((visibility("default")))
@interface AgoraRteMessage : NSObject
@property (readonly, nonatomic, copy) NSString *message;
@property (readonly, nonatomic, assign) NSTimeInterval timestamp;

- (instancetype)initWithMessage:(NSString *)message;
@end

__attribute__((visibility("default")))
@interface AgoraRteSceneInfo : NSObject
@property (readonly, nonatomic, copy) NSString *sceneId;
@property (readonly, nonatomic, assign) AgoraRteSceneConnectionState state;
@end

__attribute__((visibility("default")))
@interface AgoraRteUserInfo : NSObject
@property (readonly, nonatomic, copy) NSString *userId;
@property (readonly, nonatomic, copy) NSString *userName;
@property (readonly, nonatomic, copy) NSString *userRole;
@property (readonly, nonatomic, copy) NSString *streamId;
@end

__attribute__((visibility("default")))
@interface AgoraRteUserEvent : NSObject
@property (readonly, nonatomic, strong) AgoraRteUserInfo *modifiedUser;
@property (readonly, nonatomic, strong, nullable) AgoraRteUserInfo *operatorUser;
@property (readonly, nonatomic, strong, nullable) NSString *cause;
@end

__attribute__((visibility("default")))
@interface AgoraRteMediaStreamInfo : NSObject
@property (readonly, nonatomic, copy) NSString *streamId;
@property (readonly, nonatomic, copy, nullable) NSString *streamName;
@property (readonly, nonatomic, strong) AgoraRteUserInfo *owner;
@property (readonly, nonatomic, assign) AgoraRteMediaStreamType streamType;
@property (readonly, nonatomic, assign) AgoraRteVideoSourceType videoSourceType;
@property (readonly, nonatomic, assign) AgoraRteAudioSourceType audioSourceType;
@end

__attribute__((visibility("default")))
@interface AgoraRteMediaStreamEvent : NSObject
@property (readonly, nonatomic, strong) AgoraRteMediaStreamInfo *modifiedStream;
@property (readonly, nonatomic, strong, nullable) AgoraRteUserInfo *operatorUser;
@property (readonly, nonatomic, strong, nullable) NSString *cause;
@end

__attribute__((visibility("default")))
@interface AgoraRteLocalAudioStats : NSObject
@property (readonly, nonatomic, assign) NSUInteger numChannels;
@property (readonly, nonatomic, assign) NSUInteger sentSampleRate;
@property (readonly, nonatomic, assign) NSUInteger sentBitrate;
@end

__attribute__((visibility("default")))
@interface AgoraRteLocalVideoStats : NSObject
@property (readonly, nonatomic, assign) NSUInteger sentBitrate;
@property (readonly, nonatomic, assign) NSUInteger sentFrameRate;
@property (readonly, nonatomic, assign) NSUInteger targetBitrate;
@property (readonly, nonatomic, assign) NSUInteger encodedFrameWidth;
@property (readonly, nonatomic, assign) NSUInteger encodedFrameHeight;
@property (readonly, nonatomic, assign) NSUInteger encodedFrameCount;
@property (readonly, nonatomic, assign) NSUInteger codecType;
@end

__attribute__((visibility("default")))
@interface AgoraRteRemoteAudioStats : NSObject
@property (readonly, nonatomic, assign) NSUInteger quality;
@property (readonly, nonatomic, assign) NSUInteger networkTransportDelay;
@property (readonly, nonatomic, assign) NSUInteger audioLossRate;
@property (readonly, nonatomic, assign) NSUInteger numChannels;
@property (readonly, nonatomic, assign) NSUInteger jitterBufferDelay;
@property (readonly, nonatomic, assign) NSUInteger receivedBitrate;
@property (readonly, nonatomic, assign) NSUInteger receivedSampleRate;
@property (readonly, nonatomic, assign) NSUInteger totalFrozenTime;
@property (readonly, nonatomic, assign) NSUInteger frozenRate;
@end

__attribute__((visibility("default")))
@interface AgoraRteRemoteVideoStats : NSObject
@property (readonly, nonatomic, assign) NSUInteger delay;
@property (readonly, nonatomic, assign) NSUInteger width;
@property (readonly, nonatomic, assign) NSUInteger height;
@property (readonly, nonatomic, assign) NSUInteger receivedBitrate;
@property (readonly, nonatomic, assign) NSUInteger receivedFrameRate;
@property (readonly, nonatomic, assign) NSUInteger rxStreamType;
@end

__attribute__((visibility("default")))
@interface AgoraRteSceneStats: NSObject
/** Call duration (s), represented by an aggregate value.
 */
@property (readonly, nonatomic, assign) NSInteger duration;
/** Total number of bytes transmitted, represented by an aggregate value.
 */
@property (readonly, nonatomic, assign) NSInteger txBytes;
/** Total number of bytes received, represented by an aggregate value.
 */
@property (readonly, nonatomic, assign) NSInteger rxBytes;
/** Total number of audio bytes sent (bytes), represented by an aggregate value.
 */
@property (readonly, nonatomic, assign) NSInteger txAudioBytes;
/** Total number of video bytes sent (bytes), represented by an aggregate value.
 */
@property (readonly, nonatomic, assign) NSInteger txVideoBytes;
/** Total number of audio bytes received (bytes), represented by an aggregate value.
 */
@property (readonly, nonatomic, assign) NSInteger rxAudioBytes;
/** Total number of video bytes received (bytes), represented by an aggregate value.
 */
@property (readonly, nonatomic, assign) NSInteger rxVideoBytes;
/** Total packet transmission bitrate (Kbps), represented by an instantaneous value.
 */
@property (readonly, nonatomic, assign) NSInteger txKBitrate;
/** Total receive bitrate (Kbps), represented by an instantaneous value.
 */
@property (readonly, nonatomic, assign) NSInteger rxKBitrate;
/** Audio packet transmission bitrate (Kbps), represented by an instantaneous value.
 */
@property (readonly, nonatomic, assign) NSInteger txAudioKBitrate;
/** Audio receive bitrate (Kbps), represented by an instantaneous value.
 */
@property (readonly, nonatomic, assign) NSInteger rxAudioKBitrate;
/** Video transmission bitrate (Kbps), represented by an instantaneous value.
 */
@property (readonly, nonatomic, assign) NSInteger txVideoKBitrate;
/** Video receive bitrate (Kbps), represented by an instantaneous value.
 */
@property (readonly, nonatomic, assign) NSInteger rxVideoKBitrate;
/** Client-server latency (ms)
 */
@property (readonly, nonatomic, assign) NSInteger lastmileDelay;
/** The packet loss rate (%) from the local client to Agora's edge server, before using the anti-packet-loss method.
 */
@property (readonly, nonatomic, assign) NSInteger txPacketLossRate;
/** The packet loss rate (%) from Agora's edge server to the local client, before using the anti-packet-loss method.
 */
@property (readonly, nonatomic, assign) NSInteger rxPacketLossRate;
/** Number of users in the channel.

- Communication profile: The number of users in the channel.
- Live broadcast profile:

  - If the local user is an audience: The number of users in the channel = The number of hosts in the channel + 1.
  - If the user is a host: The number of users in the channel = The number of hosts in the channel.
 */
@property (readonly, nonatomic, assign) NSInteger userCount;
/** Application CPU usage (%).
 */
@property (readonly, nonatomic, assign) double cpuAppUsage;
/** System CPU usage (%).
 */
@property (readonly, nonatomic, assign) double cpuTotalUsage;
/** The round-trip time delay from the client to the local router.
 */
@property (readonly, nonatomic, assign) NSInteger gatewayRtt;
/** The memory usage ratio of the app (%).
 **Note** This value is for reference only. Due to system limitations, you may not get the value of this member.
 */
@property (readonly, nonatomic, assign) double memoryAppUsageRatio;
/** The memory usage ratio of the system (%).
 **Note** This value is for reference only. Due to system limitations, you may not get the value of this member.
 */
@property (readonly, nonatomic, assign) double memoryTotalUsageRatio;
/** The memory usage of the app (KB).
 **Note** This value is for reference only. Due to system limitations, you may not get the value of this member.
 */
@property (readonly, nonatomic, assign) NSInteger memoryAppUsageInKbytes;
@end

#pragma mark - Typedef
typedef void(^FailBlock) (AgoraRteError * _Nonnull error);
typedef void(^SuccessBlock) (void);
typedef void(^JsonBlock) (NSDictionary<NSString *, id> * _Nonnull json);
typedef UIView VIEW_CLASS;

NS_ASSUME_NONNULL_END
