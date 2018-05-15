//
//  UserCell.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 06/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class UserCell: UICollectionViewCell {
    @IBOutlet weak var headImageView: UIImageView!
    
    override func layoutSubviews() {
        super.layoutSubviews()
        headImageView.layer.cornerRadius = self.bounds.height * 0.5
    }
}
