//
//  MusicOptions.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

struct MusicFile {
    var filePath: String!
    var fileName: String!
    var isPlaying: Bool!
}

struct MusicOptions {
    static var currentPlayingIndex: Int?
    
    static func musicList() -> [MusicFile] {
        var array = [MusicFile]()
        let files = allMusicFile()
        for item in files {
            let musicName = item
            let path = Bundle.main.path(forResource: item, ofType: nil)
            let musicFile = MusicFile(filePath: path, fileName: musicName, isPlaying: false)
            array.append(musicFile)
        }
        return array
    }
    
    fileprivate static func allMusicFile() -> [String] {
        return ["Whats Up.mp3",
                "优しい雨.mp3",
                "Problem.mp3",
                "泣いて笑って.mp3"]
    }
}
