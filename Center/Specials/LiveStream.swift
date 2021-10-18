//
//  LiveStream.swift
//  EaseMobVoice
//
//  Created by CavanSu on 2020/9/14.
//  Copyright © 2020 EaseMob. All rights reserved.
//

import UIKit

struct LiveStream {
    var streamId: String
    var hasAudio: Bool
    var owner: LiveRole
    
    init(streamId: String, hasAudio: Bool, owner: LiveRole) {
        self.streamId = streamId
        self.hasAudio = hasAudio
        self.owner = owner
    }
}
