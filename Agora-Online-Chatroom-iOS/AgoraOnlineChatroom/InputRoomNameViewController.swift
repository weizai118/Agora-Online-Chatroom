//
//  InputRoomIdViewController.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 04/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

protocol InputRoomIdVCDelegate: NSObjectProtocol {
    func inputRoomIdVCDidJoin(_ vc: InputRoomIdViewController, roomID: String)
}

class InputRoomIdViewController: UIViewController {
    @IBOutlet weak var roomNameTextField: UITextField!
    weak var delegate: InputRoomIdVCDelegate?

    override func viewDidLoad() {
        preferredContentSize = CGSize(width: 220, height: 180)
    }
    
    @IBAction func doSurePressed(_ sender: UIButton) {
        if roomNameTextField.text?.count != 0 {
            delegate?.inputRoomIdVCDidJoin(self, roomID: roomNameTextField.text!)
        }
    }
}
