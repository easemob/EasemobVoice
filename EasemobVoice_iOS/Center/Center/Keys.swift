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
    static var AgoraAppId: String =  "your's AgoraAppId"
    static var customerId: String = "your's customerId"
    static var customerCertificate: String = "your's customerCertificate"
    static var UserToken: String = "" //可以不填
    static var BuglyId: String = "" // 可以不填
    
    // 环信IM相关
    static let clientID = "your's clientID"
    static let clientSecret = "your's clientSecret"
    static let orgName = "your's orgName"
    static let appName = "your's appName"
    static var appKey: String {
        "\(orgName)#\(appName)"
    }
}
