//
//  AgoraRte
//
//  Created by Cavan on 2020/11/14.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, AgoraRteSceneConnectionState) {
    AgoraRteSceneConnectionStateDisconnected = 0,
    AgoraRteSceneConnectionStateConnectiong = 1,
    AgoraRteSceneConnectionStateConnected = 2,
    AgoraRteSceneConnectionStateReconnecting = 3,
    AgoraRteSceneConnectionStateFail = 4,
};

typedef NS_ENUM(NSUInteger, AgoraRteMediaStreamType) {
    AgoraRteMediaStreamTypeNone = 0,
    AgoraRteMediaStreamTypeAudio = 1,
    AgoraRteMediaStreamTypeVideo = 2,
    AgoraRteMediaStreamTypeAudioAndVideo = 3,
};

typedef NS_ENUM(NSUInteger, AgoraRteVideoSourceType) {
    AgoraRteVideoSourceTypeNone = 0,
    AgoraRteVideoSourceTypeCamera = 1,
};

typedef NS_ENUM(NSUInteger, AgoraRteAudioSourceType) {
    AgoraRteAudioSourceTypeNone = 0,
    AgoraRteAudioSourceTypeMic = 1,
    AgoraRteAudioSourceTypeMix = 2,
};

typedef NS_ENUM(NSUInteger, AgoraRteRenderMode) {
    AgoraRteRenderModeHidden = 0,
    AgoraRteRenderModeFit = 1,
    AgoraRteRenderModeAdaptive = 2,
};

typedef NS_ENUM(NSUInteger, AgoraRteMediaStreamAction) {
    AgoraRteMediaStreamActionAdded = 0,
    AgoraRteMediaStreamActionUpdated = 1,
    AgoraRteMediaStreamActionRemoved = 2,
};

typedef NS_ENUM(NSUInteger, AgoraRteStreamState) {
    AgoraRteStreamStateStopped = 0,
    AgoraRteStreamStateStarting = 1,
    AgoraRteStreamStateRunning = 2,
    AgoraRteStreamStateFrozen = 3,
    AgoraRteStreamStateFailed = 4,
};

typedef NS_ENUM(NSUInteger, AgoraRteCameraSource) {
    AgoraRteCameraSourceFront = 0,
    AgoraRteCameraSourceBack = 1,
};

typedef NS_ENUM(NSUInteger, AgoraRteVideoOutputOrientationMode) {
    AgoraRteVideoOutputOrientationModeAdaptative = 0,
    AgoraRteVideoOutputOrientationModeFixedLandScape = 1,
    AgoraRteVideoOutputOrientationModeFixedPortrait = 2,
};

typedef NS_ENUM(NSUInteger, AgoraRteDegradationPreference) {
    AgoraRteDegradationPreferenceQuality = 0,
    AgoraRteDegradationPreferenceFrameRate = 1,
    AgoraRteDegradationPreferenceBalanced = 2,
};

typedef NS_ENUM(NSUInteger, AgoraRteMediaPlayerState) {
    AgoraRteMediaPlayerStatePlaying = 1,
    AgoraRteMediaPlayerStatePause = 2,
    AgoraRteMediaPlayerStateStopped = 3,
    AgoraRteMediaPlayerStateFinish = 4,
};

typedef NS_ENUM(NSInteger, AgoraRteAudioOutputRouting) {
    /** Default. */
    AgoraRteAudioOutputRoutingDefault = -1,
    /** Headset.*/
    AgoraRteAudioOutputRoutingHeadset = 0,
    /** Earpiece. */
    AgoraRteAudioOutputRoutingEarpiece = 1,
    /** Headset with no microphone. */
    AgoraRteAudioOutputRoutingHeadsetNoMic = 2,
    /** Speakerphone. */
    AgoraRteAudioOutputRoutingSpeakerphone = 3,
    /** Loudspeaker. */
    AgoraRteAudioOutputRoutingLoudspeaker = 4,
    /** Bluetooth headset. */
    AgoraRteAudioOutputRoutingHeadsetBluetooth = 5
};

typedef NS_ENUM(NSInteger, AgoraRteMediaTrackState) {
    AgoraRteMediaTrackStateStopped = 0,
    AgoraRteMediaTrackStateStarted = 1,
};

typedef NS_ENUM(NSInteger, AgoraRteAudioProfile) {
    AgoraRteAudioProfileDefault = 0,
    AgoraRteAudioProfileSpeechStandard = 1,
    AgoraRteAudioProfileMusicStandard = 2,
    AgoraRteAudioProfileMusicStandardStereo = 3,
    AgoraRteAudioProfileMusicHighQuality = 4,
    AgoraRteAudioProfileMusicHighQualityStereo = 5,
};

typedef NS_ENUM(NSInteger, AgoraRteAudioScenario) {
    AgoraRteAudioScenarioDefault = 0,
    AgoraRteAudioScenarioChatRoomEntertainment = 1,
    AgoraRteAudioScenarioEducation = 2,
    AgoraRteAudioScenarioGameStreaming = 3,
    AgoraRteAudioScenarioShowRoom = 4,
    AgoraRteAudioScenarioChatRoomGaming = 5,
};

NS_ASSUME_NONNULL_END
