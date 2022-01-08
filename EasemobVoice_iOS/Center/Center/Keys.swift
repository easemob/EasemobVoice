//
//  Keys.swift
//  Center
//
//  Created by CavanSu on 2020/2/12.
//  Copyright © 2020 EaseMob. All rights reserved.
//

import UIKit
import AVFoundation

struct Keys {
    // 声网相关
    static var AgoraAppId: String = "41a963959d2d4cd3962ea8392e0d6a06"
    static var customerId: String = "a950464be977408188fcb2ef9f7b43f0"
    static var customerCertificate: String = "980cb0ad260b4291bd9e5ea719b4feb8"
    static var UserToken: String = ""
    static var BuglyId: String = ""
    
    // 环信IM相关
    static let clientID = "YXA6poUUQxR-SnCBXwJsG_Y5CQ"
    static let clientSecret = "YXA6Am5QYDm7Tmy-gAfJ_aylRkbDm18"
    static let orgName = "1102200911042804"
    static let appName = "mqttdemo"
    static var appKey: String {
        "\(orgName)#\(appName)"
    }
}
