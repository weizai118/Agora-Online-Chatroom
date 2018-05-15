//
//  RoomInfos.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 04/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

struct RoomInfoModel {
    var image: UIImage!
    var name: String!
    var peopleCount: Int!
}

struct RoomInfos {
    static func list() -> [RoomInfoModel] {
        var array = [RoomInfoModel]()
        for index in 0...7 {
            let room = getRoomInfo(index: index)
            array.append(room)
        }
        return array
    }
    
    fileprivate static func getRoomInfo(index: Int) -> RoomInfoModel {
        let imageName = "room\(index)"
        let image = UIImage.init(named: imageName)
        let count = Int(arc4random() % 100)
        let roomName = roomTitleList()[index]
        let room = RoomInfoModel.init(image:image, name:roomName, peopleCount:count)
        return room
    }
    
    static func fakeRoomName() -> String {
        return getRoomName()
    }
    
    fileprivate static func getRoomName() -> String {
        var array = roomTitleList()
        let count = Int(arc4random() % UInt32(array.count))
        return array[count]
    }
    
    static func roomTitleList() -> [String] {
        let array = ["深夜卧谈", "出去玩就一定会买错", "为梦想打 call", "同城 同城", "醒不来的午夜电影", "湖人总冠军", "养花的微小经验", "让耳朵怀孕", "毕加索的达芬奇", "中国好声音", "一座城", "这里有你喜欢的话题", "声网Agora" , "四种杠精类型",  "因为看剧去学习某项技能吗?"]
        var dic = [String: Int]()
        for item in array {
            dic[item] = 1
        }
        let ocDic = dic as NSDictionary
        return ocDic.allKeys as! [String]
    }
}

enum RoomRole {
    case owner, broadcast, audience
    
    func rawValue() -> Int {
        switch self {
        case .owner: return 0
        case .broadcast: return 1
        case .audience: return 2
        }
    }
}
