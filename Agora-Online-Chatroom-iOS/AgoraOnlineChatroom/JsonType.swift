//
//  JsonType.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 2018/5/15.
//  Copyright © 2018 CavanSu. All rights reserved.
//

import UIKit

enum JsonType: Int {
  case currentInChannel = 0, ownerLeaveChannel, channelMsg, applyForLink, linkResult, ownerChangedBg
    
    func value() -> Int {
        switch self {
        case .currentInChannel: return 0
        case .ownerLeaveChannel: return 1
        case .channelMsg: return 2
        case .applyForLink: return 3
        case .linkResult: return 4
        case .ownerChangedBg: return 5
        }
    }
}
