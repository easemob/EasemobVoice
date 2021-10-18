//
//  AppDelegate.swift
//  EaseMobVoice
//
//  Created by CavanSu on 2020/8/31.
//  Copyright © 2020 EaseMob. All rights reserved.
//

import UIKit
import Bugly
import HyphenateChat

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
//        enableBugly()
        
        EMServer.shared.debug()
        
        let options = EMOptions(appkey: Keys.appKey)
        options?.usingHttpsOnly = true
        options?.isAutoLogin = true
        options?.isAutoDownloadThumbnail = true
        EMClient.shared().initializeSDK(with: options)       
        
        return true
    }
    
//    func enableBugly() {
//        let config = BuglyConfig()
//        let buglyId = Keys.BuglyId
//        Bugly.start(withAppId: buglyId,
//                    config: config)
//    }
}

