//
//  BuildRoomViewController.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class BuildRoomViewController: UIViewController {
    @IBOutlet weak var inputTitleTextField: UITextField!
    @IBOutlet weak var inputIdTextField: UITextField!
    @IBOutlet weak var inputLineView: UIView!
    @IBOutlet weak var inputLine2View: UIView!
    @IBOutlet weak var inputViewBottom: NSLayoutConstraint!
    @IBOutlet weak var titleCollectionView: UICollectionView!
    
    var originLineBottomConstant: CGFloat!
    var inputLineViewFrameOnView: CGRect!
    var inputLine2ViewFrameOnView: CGRect!
    
    lazy var titleList: [String] = {() -> [String] in
        return RoomInfos.roomTitleList()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        addKeyboardObserver()
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        NotificationCenter.default.removeObserver(self)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier, !segueId.isEmpty else {
            return
        }
        
        switch segueId {
        case "RoomViewController":
            let roomVC = segue.destination as! RoomViewController
            roomVC.roomID = inputIdTextField.text
            roomVC.roomName = inputTitleTextField.text
            roomVC.externalRole = .owner
        default: break
        }
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        guard let title = inputTitleTextField.text, let roomID = inputIdTextField.text, !title.isEmpty, !roomID.isEmpty else {
            return false
        }
        return true
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    @IBAction func doClosePressed(_ sender: UIButton) {
        if let navigationController = self.navigationController {
            navigationController.dismiss(animated: true, completion: nil)
        } else {
            self.dismiss(animated: true, completion: nil)
        }
    }
}

private extension BuildRoomViewController {
    func addKeyboardObserver() {
        originLineBottomConstant = inputViewBottom.constant
        inputLineViewFrameOnView = (inputLineView.superview?.convert((inputLineView.frame), to: self.view))!
        inputLine2ViewFrameOnView = (inputLine2View.superview?.convert((inputLine2View.frame), to: self.view))!
        
        NotificationCenter.default.addObserver(forName: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil, queue: nil) { [weak self] (notify) in
            if let userInfo = notify.userInfo, let strongSelf = self {
                let endKeyboardFrameValue = userInfo[UIKeyboardFrameEndUserInfoKey] as? NSValue
                let endKeyboardFrame = endKeyboardFrameValue?.cgRectValue
                let durationValue = userInfo[UIKeyboardAnimationDurationUserInfoKey] as? NSNumber
                let duration = durationValue?.doubleValue
                let screenHeight = UIScreen.main.bounds.height
                var endInputLineViewBottomConstant: CGFloat!
                
                if (endKeyboardFrame?.maxY)! > screenHeight {
                    endInputLineViewBottomConstant = strongSelf.originLineBottomConstant
                } else {
                    var rect : CGRect
                    if strongSelf.inputTitleTextField.isEditing {
                        rect = strongSelf.inputLineViewFrameOnView
                    } else {
                        rect = strongSelf.inputLine2ViewFrameOnView
                    }
                    endInputLineViewBottomConstant = (strongSelf.originLineBottomConstant)! +  CGFloat(rect.maxY) - (endKeyboardFrame?.minY)!
                }
                
                UIView.animate(withDuration: duration!, animations: {
                    strongSelf.inputViewBottom.constant = endInputLineViewBottomConstant
                    strongSelf.view.layoutIfNeeded()
                }, completion: nil);
            }
        }
    }
}

extension BuildRoomViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return titleList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "TitleCell", for: indexPath) as! TitleCell
        cell.titleLabel.text = titleList[indexPath.item]
        return cell
    }
}

extension BuildRoomViewController: UICollectionViewDelegate, UICollectionViewDelegateFlowLayout  {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let font = UIFont.systemFont(ofSize: 15)
        let attributes = [kCTFontAttributeName: font]
        let string = titleList[indexPath.item]
        let ocString = string as NSString
        let stringSize = ocString.size(withAttributes: attributes as [NSAttributedStringKey : Any])
        let size = CGSize(width: stringSize.width + 40, height: 44)
        return size
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 10
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 18
    }
}

extension BuildRoomViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return true
    }
}

