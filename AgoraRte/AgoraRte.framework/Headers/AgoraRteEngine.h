//
//  AgoraRte
//
//  Created by Cavan on 2020/11/14.
//

#import <Foundation/Foundation.h>
#import "AgoraRteObjects.h"
#import "AgoraRteScene.h"
#import "AgoraRteMediaControl.h"

NS_ASSUME_NONNULL_BEGIN

@class AgoraRteEngine;
@protocol AgoraRteEngineDelegate <NSObject>
@optional
- (void)rteEngine:(AgoraRteEngine *)engine
didReceivedMessage:(AgoraRteMessage *)message
        fromUserId:(NSString *_Nonnull)userId;
@end

__attribute__((visibility("default")))
@interface AgoraRteEngine : NSObject
@property (nonatomic, weak) id <AgoraRteEngineDelegate> _Nullable delegate;

+ (void)createWithConfig:(AgoraRteEngineConfig *)config
                 success:(void (^) (AgoraRteEngine *))success
                    fail:(FailBlock)fail;

+ (NSString *)getVersion;

- (AgoraRteScene *)createAgoraRteScene:(AgoraRteSceneConfig *)config;

- (AgoraRteMediaControl *)getAgoraMediaControl;

- (void)uploadSDKLogToAgoraServiceWithSuccess:(void (^) (NSString * logId))success
                                         fail:(FailBlock)fail;

- (void)destroy;

#pragma mark Unavailable Initializers
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
@end

NS_ASSUME_NONNULL_END
