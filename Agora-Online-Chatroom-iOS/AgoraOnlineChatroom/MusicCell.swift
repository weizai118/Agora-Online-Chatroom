//
//  MusicCell.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class MusicCell: UITableViewCell {
    @IBOutlet weak var fileNameLabel: UILabel!
    @IBOutlet weak var playImageView: UIImageView!
    
    var isPlaying: Bool = false {
        didSet {
            let blue = UIColor(red: 116 / 255, green: 197 / 255, blue: 230 / 255, alpha: 1)
            let white = UIColor.white
            fileNameLabel.textColor = isPlaying ? blue : white
            playImageView.isHidden = !isPlaying
        }
    }
}
