//
//  AgoraRte
//
//  Created by Cavan on 2020/11/14.
//

#import <Foundation/Foundation.h>
#import "AgoraRteMediaTrack.h"
#import "AgoraRteObjects.h"

NS_ASSUME_NONNULL_BEGIN

@class AgoraRteLocalUser;
@protocol AgoraRteLocalUserDelegate <NSObject>
@optional
- (void)localUser:(AgoraRteLocalUser *)user
didUpdateLocalUserInfo:(AgoraRteUserEvent *)userEvent;

- (void)localUser:(AgoraRteLocalUser *)user
didUpdateLocalUserProperties:(NSArray<NSString *> *)changedProperties
           remove:(BOOL)remove
            cause:(NSString * _Nullable)cause;

- (void)localUser:(AgoraRteLocalUser *)user
didChangeOfLocalStream:(AgoraRteMediaStreamEvent *)event
       withAction:(AgoraRteMediaStreamAction)action;
@end

@protocol AgoraRteMediaStreamDelegate <NSObject>
@optional
- (void)localUser:(AgoraRteLocalUser *)user
didChangeOfLocalAudioStream:(NSString *)streamId
        withState:(AgoraRteStreamState)state;

- (void)localUser:(AgoraRteLocalUser *)user
didChangeOfLocalVideoStream:(NSString *)streamId
        withState:(AgoraRteStreamState)state;

- (void)localUser:(AgoraRteLocalUser *)user
didChangeOfRemoteAudioStream:(NSString *)streamId
        withState:(AgoraRteStreamState)state;

- (void)localUser:(AgoraRteLocalUser *)user
didChangeOfRemoteVideoStream:(NSString *)streamId
        withState:(AgoraRteStreamState)state;

- (void)localUser:(AgoraRteLocalUser *)user
audioVolumeIndicationOfStream:(NSString *)streamId
       withVolume:(NSUInteger)volume;
@end

__attribute__((visibility("default")))
@interface AgoraRteLocalUser : NSObject
@property (nonatomic, strong, readonly) AgoraRteUserInfo *info;
@property (nonatomic, weak, nullable) id <AgoraRteLocalUserDelegate> localUserDelegate;
@property (nonatomic, weak, nullable) id <AgoraRteMediaStreamDelegate> mediaStreamDelegate;

// Message
- (AgoraRteError * _Nullable)sendPeerMessageToRemoteUser:(AgoraRteMessage *_Nonnull)message
                                                toUserId:(NSString * _Nonnull)userId
                                                 success:(SuccessBlock _Nullable)success
                                                    fail:(FailBlock _Nullable)fail;

- (AgoraRteError * _Nullable)sendSceneMessageToAllRemoteUsers:(AgoraRteMessage *_Nonnull)message
                                                      success:(SuccessBlock _Nullable)success
                                                         fail:(FailBlock _Nullable)fail;

// Publish
- (AgoraRteError * _Nullable)publishLocalMediaTrack:(AgoraRteMediaTrack *)track
                                       withStreamId:(NSString *)streamId
                                            success:(SuccessBlock _Nullable)success
                                               fail:(FailBlock _Nullable)fail;

- (AgoraRteError * _Nullable)unpublishLocalMediaTrack:(AgoraRteMediaTrack *)track
                                         withStreamId:(NSString *)streamId
                                              success:(SuccessBlock _Nullable)success
                                                 fail:(FailBlock _Nullable)fail;

- (AgoraRteError * _Nullable)createOrUpdateRemoteStream:(AgoraRteRemoteStreamInfo *)remoteStreamInfo
                                                success:(SuccessBlock _Nullable)success
                                                   fail:(FailBlock _Nullable)fail;

- (AgoraRteError * _Nullable)deleteRemoteStream:(AgoraRteRemoteStreamInfo *)streamId
                                        success:(SuccessBlock _Nullable)success
                                           fail:(FailBlock _Nullable)fail;

// Mute
- (AgoraRteError * _Nullable)muteLocalMediaStream:(NSString *)streamId
                                             type:(AgoraRteMediaStreamType)type
                                          success:(SuccessBlock _Nullable)success
                                             fail:(FailBlock _Nullable)fail;

- (AgoraRteError * _Nullable)unmuteLocalMediaStream:(NSString *)streamId
                                               type:(AgoraRteMediaStreamType)type
                                            success:(SuccessBlock _Nullable)success
                                               fail:(FailBlock _Nullable)fail;

// Subscribe
- (AgoraRteError * _Nullable)subscribeRemoteStream:(NSString *)streamId
                                              type:(AgoraRteMediaStreamType)type;

- (AgoraRteError * _Nullable)unsubscribeRemoteStream:(NSString *)streamId
                                                type:(AgoraRteMediaStreamType)type;

// Properties
- (AgoraRteError * _Nullable)setUserProperties:(NSString *)userId
                                    properties:(NSDictionary<NSString*, NSString*> *)properties
                                        remove:(BOOL)remove
                                         cause:(NSString * _Nullable)cause
                                       success:(SuccessBlock _Nullable)success
                                          fail:(FailBlock _Nullable)fail;

- (AgoraRteError * _Nullable)setSceneProperties:(NSDictionary<NSString*, NSString*> *)properties
                                         remove:(BOOL)remove
                                          cause:(NSString * _Nullable)cause
                                        success:(SuccessBlock _Nullable)success
                                           fail:(FailBlock _Nullable)fail;

// Render
- (AgoraRteError * _Nullable)renderRemoteStream:(NSString *)streamId
                                         onView:(VIEW_CLASS *)view
                                   renderConfig:(AgoraRteRenderConfig *)config;

- (AgoraRteError * _Nullable)stopRenderRemoteStream:(NSString *)streamId;

- (void)destroy;

#pragma mark Unavailable Initializers
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
@end

NS_ASSUME_NONNULL_END
