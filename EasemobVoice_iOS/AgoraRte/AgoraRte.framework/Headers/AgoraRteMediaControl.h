//
//  AgoraRte
//
//  Created by Cavan on 2020/11/14.
//

#import <Foundation/Foundation.h>
#import "AgoraRteMediaTrack.h"
#import "AgoraRteEnums.h"

NS_ASSUME_NONNULL_BEGIN

@class AgoraRteMediaControl;
@protocol AgoraRteMediaControlDelegate <NSObject>
@optional
- (void)mediaControl:(AgoraRteMediaControl *)control
didChnageAudioRouting:(AgoraRteAudioOutputRouting)routing;
@end

__attribute__((visibility("default")))
@interface AgoraRteMediaControl : NSObject
@property (nonatomic, weak) id<AgoraRteMediaControlDelegate> delegate;

- (AgoraRteCameraVideoTrack *)createCameraVideoTrack;
- (AgoraRteMicrophoneAudioTrack *)createMicphoneAudioTrack;
- (AgoraRteMediaPlayer *)createMediaPlayerTrack;

#pragma mark Unavailable Initializers
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
@end

NS_ASSUME_NONNULL_END
