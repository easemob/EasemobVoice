//
//  AgoraRte
//
//  Created by Cavan on 2020/11/14.
//

#import <Foundation/Foundation.h>
#import "AgoraRteLocalUser.h"
#import "AgoraRteObjects.h"

NS_ASSUME_NONNULL_BEGIN

@class AgoraRteScene;
@protocol AgoraRteSceneDelegate <NSObject>
@optional
- (void)scene:(AgoraRteScene *)scene
didChangeConnectionState:(AgoraRteSceneConnectionState)state
    withError:(AgoraRteError * _Nullable)error;

- (void)scene:(AgoraRteScene *)scene
didUpdateSceneProperties:(NSArray<NSString *> *)changedProperties
       remove:(BOOL)remove
        cause:(NSString * _Nullable)cause;

- (void)scene:(AgoraRteScene *)scene
didReceiveSceneMessage:(AgoraRteMessage *)message
     fromUser:(AgoraRteUserInfo *)user;

// Remote user
- (void)scene:(AgoraRteScene *)scene
didUpdateRemoteUserInfo:(AgoraRteUserEvent *)userEvent;

- (void)scene:(AgoraRteScene *)scene
didRemoteUsersJoin:(NSArray<AgoraRteUserEvent *> *)userEvents;

- (void)scene:(AgoraRteScene *)scene
didRemoteUsersLeave:(NSArray<AgoraRteUserEvent *> *)userEvents;

- (void)scene:(AgoraRteScene *)scene
didUpdateRemoteUserProperties:(NSArray<NSString *> *)changedProperties
       remove:(BOOL)remove
        cause:(NSString * _Nullable)cause
     fromUser:(AgoraRteUserInfo *)user;

// Remote stream
- (void)scene:(AgoraRteScene *)scene
didAddRemoteStreams:(NSArray<AgoraRteMediaStreamEvent *> *)streamEvents;

- (void)scene:(AgoraRteScene *)scene
didRemoveRemoteStreams:(NSArray<AgoraRteMediaStreamEvent *> *)streamEvents;

- (void)scene:(AgoraRteScene *)scene
didUpdateRemoteStreams:(NSArray<AgoraRteMediaStreamEvent *> *)streamEvents;
@end

@protocol AgoraRteStatsDelegate <NSObject>
@optional
- (void)scene:(AgoraRteScene *)scene
didUpdateLocalAudioStream:(NSString *)streamId
    withStats:(AgoraRteLocalAudioStats *)stats;

- (void)scene:(AgoraRteScene *)scene
didUpdateLocalVideoStream:(NSString *)streamId
    withStats:(AgoraRteLocalVideoStats *)stats;

- (void)scene:(AgoraRteScene *)scene
didUpdateRemoteAudioStream:(NSString *)streamId
    withStats:(AgoraRteRemoteAudioStats *)stats;

- (void)scene:(AgoraRteScene *)scene
didUpdateRemoteVideoStream:(NSString *)streamId
    withStats:(AgoraRteRemoteVideoStats *)stats;

- (void)scene:(AgoraRteScene *)scene
  reportStats:(AgoraRteSceneStats *)stats;
@end

__attribute__((visibility("default")))
@interface AgoraRteScene : NSObject
@property (readonly, nonatomic, strong) AgoraRteSceneInfo *info;
@property (readonly, nonatomic, strong) NSArray<AgoraRteUserInfo *> *users;
@property (readonly, nonatomic, strong) NSArray<AgoraRteMediaStreamInfo *> *streams;

@property (readonly, nonatomic, strong, nullable) NSDictionary<NSString *, id> *properties;
@property (readonly, nonatomic, strong, nullable) AgoraRteLocalUser *localUser;
@property (readonly, nonatomic, copy, nullable) NSString *userToken;

@property (nonatomic, weak) id<AgoraRteSceneDelegate> sceneDelegate;
@property (nonatomic, weak) id<AgoraRteStatsDelegate> statsDelegate;

- (void)joinWithOptions:(AgoraRteSceneJoinOptions *)options
                success:(void (^) (AgoraRteLocalUser * user))success
                   fail:(FailBlock)fail;

- (void)leave;

- (void)destroy;

- (NSDictionary<NSString *, id> * _Nullable)getUserPropertyByUserId:(NSString * _Nonnull)userId;
- (NSDictionary<NSString *, id> * _Nullable)getUserPropertyByKey:(NSArray<NSString *> * _Nonnull)keys
                                                                  userId:(NSString * _Nonnull)userId;

- (void)setParameters:(NSString *)parameters;

#pragma mark Unavailable Initializers
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
@end

NS_ASSUME_NONNULL_END
