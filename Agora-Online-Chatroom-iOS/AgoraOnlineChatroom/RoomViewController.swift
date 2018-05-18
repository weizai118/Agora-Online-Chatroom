//
//  RoomViewController.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 05/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class RoomViewController: UIViewController {
    @IBOutlet weak var currenHeadImageView: UIImageView!
    @IBOutlet weak var currentNameLabel: UILabel!
    @IBOutlet weak var userCollectionView: UICollectionView!
    @IBOutlet weak var totoalUsersLabel: UILabel!
    @IBOutlet weak var userMomentsTableView: UITableView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var inputMsgView: UIView!
    @IBOutlet weak var inputMsgTextField: UITextField!
    @IBOutlet weak var inputMsgViewBottom: NSLayoutConstraint!
    @IBOutlet weak var bgImageView: UIImageView!
    @IBOutlet weak var applyForButton: UIButton!
    @IBOutlet weak var albumButton: UIButton!
    @IBOutlet weak var musicButton: UIButton!
    @IBOutlet weak var micButton: UIButton!
    @IBOutlet weak var remindInputTextField: UITextField!
    
    private var role: RoomRole! {
        didSet {
            switch role {
            case .owner:
                albumButton.isHidden = false
                musicButton.isHidden = false
                applyForButton.isHidden = true
                micButton.isEnabled = true
                agoraMediaKit.setClientRole(.broadcaster)
            case .broadcast:
                albumButton.isHidden = true
                musicButton.isHidden = true
                applyForButton.isHidden = false
                applyForButton.isSelected = true
                micButton.isEnabled = true
                agoraMediaKit.setClientRole(.broadcaster)
            case .audience:
                albumButton.isHidden = true
                musicButton.isHidden = true
                applyForButton.isHidden = false
                applyForButton.isSelected = false
                micButton.isEnabled = false
                agoraMediaKit.setClientRole(.audience)
            default: break
            }
        }
    }
    
    var externalRole: RoomRole!
    var roomOwner: String?
    var applyForLinkingUser: String!
    var currentUser: UserInfo!
    var currentUid: UInt!
    var agoraMediaKit: AgoraRtcEngineKit!
    var agoraSigKit: AgoraAPI!
    var roomID: String?
    var roomName: String?
    var currentBgIndex: Int! = -1
    
    lazy var totalUserList: [UserInfo] = {() -> [UserInfo] in
        var array = [UserInfo]()
        return array
    }()
    
    lazy var totalUidIndexDic: [UInt: Int] = {() -> [UInt: Int] in
        return [UInt: Int]()
    }()
    
    lazy var colUserList: [UserInfo] = {() -> [UserInfo] in
        var array = [UserInfo]()
        return array
    }()
    
    lazy var momentsList: [MomentsInfo] = {() -> [MomentsInfo] in
        return [MomentsInfo]()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
       
        loadAgoraMediaKit()
        loadAgoraSigKit()
        updateCurrenUserInfo()
        updateRoomInfo()
        updateViewsSettings()
        addKeyboardObserver()
        mediaJoinChannel()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateCollectionViewLayout()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier, !segueId.isEmpty else {
            return
        }
        
        switch segueId {
        case "BackgroundViewController":
            let backgroundVC = segue.destination as! BackgroundViewController
            backgroundVC.delegate = self
            setPopoverDelegate(of: backgroundVC)
        case "BackMusicViewController":
            let backMusicVC = segue.destination as! BackMusicViewController
            backMusicVC.delegate = self
            setPopoverDelegate(of: backMusicVC)
        default: break
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    //MARK: Response Action
    @IBAction func doCloseRoomPressed(_ sender: UIButton) {
        leaveRoom()
    }
    
    @IBAction func doMicPressed(_ sender: UIButton) {
        sender.isSelected = !sender.isSelected
        agoraMediaKit.muteLocalAudioStream(sender.isSelected)
    }
    
    @IBAction func doApplyForLinkingPressed(_ sender: UIButton) {
        if role == .audience {
            sendPeerApplyForLinkingRequest()
        } else if role == .broadcast {
            role = .audience
            
        }
    }
}

private extension RoomViewController {
    func loadAgoraMediaKit() {
        agoraMediaKit = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.appId(), delegate: self)
        agoraMediaKit.setChannelProfile(.liveBroadcasting)
        
        if externalRole == .owner {
            agoraMediaKit.setAudioProfile(.musicHighQualityStereo, scenario: .default)
        }
    }
    
    func mediaJoinChannel() {
        if let channelID = roomID {
            agoraMediaKit.joinChannel(byToken: nil, channelId: channelID, info: nil, uid: 0, joinSuccess: nil)
        } else {
            agoraMediaKit.joinChannel(byToken: nil, channelId: "CavanAgoraOnlineChatroom", info: nil, uid: 0, joinSuccess: nil)
        }
    }
    
    func loadAgoraSigKit() {
        agoraSigKit = AgoraAPI.getInstanceWithoutMedia(KeyCenter.appId())
        agoraSigKitCallback()
    }
    
    func updateCollectionViewLayout() {
        let itemWidth = UIScreen.main.bounds.size.width * 0.1 - 1
        let itemHeight = itemWidth
        let margin = 17.0
        let marginTop = (userCollectionView.bounds.height - itemHeight) * 0.5
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: itemWidth, height: itemHeight)
        layout.scrollDirection = .horizontal
        userCollectionView.contentInset = UIEdgeInsets(top: marginTop, left: CGFloat(margin), bottom: marginTop, right: CGFloat(margin))
        userCollectionView.setCollectionViewLayout(layout, animated: false)
    }
    
    func updateCurrenUserInfo() {
        currentUser = UserInfo.fakeCurrentUser()
        role = externalRole
    }
    
    func updateRoomInfo() {
        if role == .owner {
            currentNameLabel.text = currentUser.name
            currenHeadImageView.image = currentUser.headImage
        }
        
        if let roomName = roomName, !roomName.isEmpty {
            titleLabel.text = roomName
        } else {
            titleLabel.text = RoomInfos.fakeRoomName()
        }
    }
    
    func updateViewsSettings() {
        userMomentsTableView.rowHeight = UITableViewAutomaticDimension
        userMomentsTableView.estimatedRowHeight = 55
        inputMsgTextField.delegate = self
    }
    
    func updateTotalUsersLabel() {
        totoalUsersLabel.text = "\(colUserList.count) 人"
    }
    
    func setPopoverDelegate(of vc: UIViewController) {
        vc.popoverPresentationController?.delegate = self
    }
    
    func addKeyboardObserver() {
        NotificationCenter.default.addObserver(forName: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil, queue: nil) { [weak self] (notify) in
            if let userInfo = notify.userInfo, let strongSelf = self {
                let endKeyboardFrameValue = userInfo[UIKeyboardFrameEndUserInfoKey] as? NSValue
                let endKeyboardFrame = endKeyboardFrameValue?.cgRectValue
                let durationValue = userInfo[UIKeyboardAnimationDurationUserInfoKey] as? NSNumber
                let duration = durationValue?.doubleValue
                let screenHeight = UIScreen.main.bounds.height
                var endInputMsgViewBottomConstant: CGFloat!
                
                endInputMsgViewBottomConstant = CGFloat(screenHeight - (endKeyboardFrame?.minY)!)
                
                var isShowing: Bool!
                if (endKeyboardFrame?.maxY)! > screenHeight {
                    isShowing = false
                    endInputMsgViewBottomConstant = endInputMsgViewBottomConstant - CGFloat((self?.inputMsgView.bounds.height)!)
                } else {
                    isShowing = true
                    strongSelf.inputMsgTextField.becomeFirstResponder()
                    strongSelf.inputMsgView.isHidden = false
                }
                
                UIView.animate(withDuration: duration!, animations: {
                    strongSelf.inputMsgViewBottom.constant = endInputMsgViewBottomConstant
                    strongSelf.view.layoutIfNeeded()
                }, completion: {(isSuccess) in
                    if isShowing == false {
                        strongSelf.inputMsgView.isHidden = true
                    }
                });
            }
        }
    }
    
    func appendCurrentUser() {
        let index = totalUserList.count
        totalUidIndexDic[currentUid] = index
        var user = currentUser
        totalUserList.append(user!)
        
        user?.uid = currentUid
        colUserList.append(user!)
        let item = colUserList.count - 1
    
        let indexPath = IndexPath(item: item, section: 0)
        userCollectionView.insertItems(at: [indexPath])
    }
    
    func appendNewUser(uid: UInt) {
        var user : UserInfo!
        
        if let _ = totalUidIndexDic[uid] {
            user = UserInfo.fakeRemoteUser()
        } else {
            let index = totalUserList.count
            totalUidIndexDic[uid] = index
            user = UserInfo.fakeRemoteUser()
            totalUserList.append(user)
        }
        
        guard let _ = getColUserWith(uid: uid) else {
            user.uid = uid
            colUserList.append(user)
            let item = colUserList.count - 1
            let indexPath = IndexPath(item: item, section: 0)
            userCollectionView.insertItems(at: [indexPath])
            return
        }
    }
    
    func getTotalUserWith(uid: UInt) -> UserInfo {
        let index = totalUidIndexDic[uid]
        let user = totalUserList[index!]
        return user
    }
    
    func getColUserWith(uid: UInt) -> UserInfo? {
        var user: UserInfo?
        for item in colUserList {
            if item.uid == uid {
                user = item
                return user
            }
        }
        return nil
    }
    
    func appendNewMoments(uid: UInt, msg: String) {
        guard let _ = totalUidIndexDic[uid] else {
            return
        }
        
        let moment = MomentsInfo(uid: uid, msg: msg)
        momentsList.insert(moment, at: 0)
        let indexPath = IndexPath(row: 0, section: 0)
        userMomentsTableView.insertRows(at: [indexPath], with: .none)
    }
    
    func removeUser(uid: UInt) {
        var deletedIndex: Int?
        for (index, item) in colUserList.enumerated() {
            if item.uid == uid {
                deletedIndex = index
                break
            }
        }
        
        if let index = deletedIndex {
            colUserList.remove(at: index)
            let indexPath = IndexPath(item: index, section: 0)
            userCollectionView.deleteItems(at: [indexPath])
        }
    }
    
    func leaveRoom() {
        if role == .owner {
            sendChannelOwnerLeaveMessage()
        }
        agoraMediaKit.leaveChannel(nil)
        agoraSigKit.channelLeave(roomID!)
        agoraSigKit.logout()
        
        if let navigationController = self.navigationController {
            navigationController.dismiss(animated: true, completion: nil)
        } else {
            self.dismiss(animated: true, completion: nil)
        }
    }
    
//MARK: SigCallback
    func agoraSigKitCallback() {
        agoraSigKit.onLoginSuccess = {[weak self] (uid, fd) in
            if let strongSelf = self {
                DispatchQueue.main.async {
                    strongSelf.agoraSigKit.channelJoin(strongSelf.roomID)
                }
            }
        }
        
        agoraSigKit.onChannelJoined = {[weak self] (channelId) in
            if let strongSelf = self {
                DispatchQueue.main.async {
                    strongSelf.remindInputTextField.isHidden = false
                    strongSelf.appendCurrentUser()
                    strongSelf.appendNewMoments(uid: strongSelf.currentUid, msg: "加入频道")
                }
            }
        }
        
        agoraSigKit.onChannelLeaved = {[weak self] (channelId, eCode) in
            if let strongSelf = self, strongSelf.role == .owner {
                DispatchQueue.main.async {
                    strongSelf.sendChannelOwnerLeaveMessage()
                }
            }
        }
        
        agoraSigKit.onChannelUserJoined = {[weak self] (account, uid) in
            if let strongSelf = self, let account = account, !account.isEmpty {
                DispatchQueue.main.async {
                    strongSelf.appendNewUser(uid: UInt(account)!)
                    strongSelf.sendPeerCurrentUserInChannel(toPeerUid: account)
                    strongSelf.appendNewMoments(uid: UInt(account)!, msg: "加入频道")
                    strongSelf.sendPeerOwnerBackground(toPeerUid: account)
                }
            }
        }
        
        agoraSigKit.onChannelUserLeaved = {[weak self] (account, uid) in
            if let strongSelf = self, let account = account, !account.isEmpty {
                DispatchQueue.main.async {
                    strongSelf.appendNewMoments(uid: UInt(account)!, msg: "离开频道")
                    strongSelf.removeUser(uid: UInt(account)!)
                }
            }
        }
        
        agoraSigKit.onMessageChannelReceive = {[weak self] (channelID, account, uid, msg) in
            if let strongSelf = self, let msg = msg, msg.count > 0, let account = account {
                DispatchQueue.main.async {
                    if let jsonData = msg.data(using: .utf8), let dic = try? JSONSerialization.jsonObject(with: jsonData, options: .mutableContainers) {
                        let receiveDic = dic as! [String : Any]
                        let type = receiveDic["type"] as! Int
                        let message = receiveDic["msg"] as! String
                        
                        switch type {
                            case 1: strongSelf.receiveOwnerLeaveChannel()
                            case 2: strongSelf.appendNewMoments(uid: UInt(account)!, msg: message)
                            case 5: strongSelf.receiveOwnerChangeBackground(index: message)
                            default: break
                        }
                    }
                }
            }
        }
        
        agoraSigKit.onMessageInstantReceive = {[weak self] (account, uid, msg) in
            if let strongSelf = self, let msg = msg, msg.count > 0, let account = account {
                DispatchQueue.main.async {
                    if let jsonData = msg.data(using: .utf8), let dic = try? JSONSerialization.jsonObject(with: jsonData, options: .mutableContainers) {
                        let receiveDic = dic as! [String : Any]
                        let type = receiveDic["type"] as! Int
                        let message = receiveDic["msg"] as! String
                        let role = receiveDic["role"] as! Int
 
                        switch type {
                            case 0: strongSelf.receiveOthersInChannel(account: account, remoteRole: role)
                            case 3: strongSelf.receiveRemoteUserApplyForLinking(account: account)
                            case 4: strongSelf.receiveResultOfApplyForLinking(result: message)
                            case 5: strongSelf.receiveOwnerChangeBackground(index: message)
                            default: break
                        }
                    }
                }
            }
        }
        
        agoraSigKit.onError = { (name, eCode, desc) in
            if let name = name, let desc = desc {
                print("type: \(name), Error: \(eCode), desc: \(desc)")
            }
        }
        
        agoraSigKit.onLog = { (log) in
            guard var log = log else {
                return
            }
            let time = log[..<log.index(log.startIndex, offsetBy: 10)]
            let dformatter = DateFormatter()
            let timeInterval = TimeInterval(Int(time)!)
            let date = Date(timeIntervalSince1970: timeInterval)
            dformatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
            log.replaceSubrange(log.startIndex..<log.index(log.startIndex, offsetBy: 10), with: dformatter.string(from: date) + ".")
            LogWriter.write(log: log)
        }
        
    }
    
//MARK: Send Channel Message
    func sendChannelOwnerLeaveMessage() {
        if let currentUid = currentUid, role == .owner {
            let jsonDic: [String : Any] = ["account": currentUid, "role": role.rawValue(), "type": JsonType.ownerLeaveChannel.rawValue, "msg": ""]
            sendChannelInfo(jsonDic: jsonDic)
        }
    }
    
    func sendChannelOwnerChangedBackground() {
        if let bgIndex = currentBgIndex, role == .owner {
            let message = "\(bgIndex)"
            let jsonDic: [String : Any] = ["account": currentUid, "role": role.rawValue(), "type": JsonType.ownerChangedBg.rawValue, "msg": message]
            sendChannelInfo(jsonDic: jsonDic)
        }
    }
    
//MARK: Send Peer Message
    func sendPeerCurrentUserInChannel(toPeerUid: String) {
        if let currentUid = currentUid {
            let message = "\(role.rawValue())"
            let jsonDic: [String : Any] = ["account": currentUid, "role": role.rawValue(), "type": JsonType.currentInChannel.rawValue, "msg": message]
            sendPeerInfo(jsonDic: jsonDic, toPeer: toPeerUid)
        }
    }
    
    func sendPeerOwnerBackground(toPeerUid: String) {
        if let bgIndex = currentBgIndex, role == .owner {
            let message = "\(bgIndex)"
            let jsonDic: [String : Any] = ["account": currentUid, "role": role.rawValue(), "type": JsonType.ownerChangedBg.rawValue, "msg": message]
            sendPeerInfo(jsonDic: jsonDic, toPeer: toPeerUid)
        }
    }
    
    func sendPeerApplyForLinkingRequest() {
        if let currentUid = currentUid {
            if role == .audience {
                let jsonDic: [String : Any] = ["account": currentUid, "role": role.rawValue(), "type": JsonType.applyForLink.rawValue, "msg": ""]
                if let roomOwner = roomOwner {
                    sendPeerInfo(jsonDic: jsonDic, toPeer: roomOwner)
                }
            }
        }
    }
    
    func sendChannelInfo(jsonDic: [String: Any]) {
        let data = try? JSONSerialization.data(withJSONObject: jsonDic, options: .prettyPrinted)
        if let data = data {
            let jsonString = NSString(data: data, encoding: String.Encoding.utf8.rawValue)
            if let roomID = roomID {
                agoraSigKit.messageChannelSend(roomID, msg: jsonString! as String, msgID: nil)
            }
        }
    }
    
    func sendPeerInfo(jsonDic: [String: Any], toPeer: String) {
        let data = try? JSONSerialization.data(withJSONObject: jsonDic, options: .prettyPrinted)
        if let data = data {
            let jsonString = NSString(data: data, encoding: String.Encoding.utf8.rawValue)
            agoraSigKit.messageInstantSend(toPeer, uid: 0, msg: jsonString! as String, msgID: nil)
        }
    }
    
    //MARK: Receive Message
    func receiveOthersInChannel(account: String, remoteRole: Int) {
            if role != .owner {
                if remoteRole == 0 {
                    roomOwner = account
                    appendNewUser(uid: UInt(account)!)
                    appendNewMoments(uid: UInt(account)!, msg: "房主")
                    let owner = getTotalUserWith(uid: UInt(account)!)
                    currenHeadImageView.image = owner.headImage
                    currentNameLabel.text = owner.name
                } else {
                    appendNewUser(uid: UInt(account)!)
                    appendNewMoments(uid: UInt(account)!, msg: "加入频道")
                }
            }
    }
    
    func receiveOwnerLeaveChannel() {
        leaveRoom()
    }
    
    func receiveOwnerChangeBackground(index: String?) {
        if let bgIndex = Int(index!), bgIndex != -1, role != .owner {
            bgImageView.isHidden = false
            bgImageView.image = UIImage.init(named: "bg\(bgIndex)")
        }
    }
    
    func receiveRemoteUserApplyForLinking(account: String) {
        if role == .owner, let uid = UInt(account) {
            applyForLinkingUser = account
            var user: UserInfo!
            for item in colUserList {
                if item.uid == uid {
                    user = item
                }
            }

            let alertController = UIAlertController.init(title: "申请连麦", message: user.name, preferredStyle: .alert)
            let agreeAction = UIAlertAction.init(title: "同意", style: .default) { [weak self] (action) in
                self?.decisionOfAppleForLinking(isAgree: true)
            }
            
            let rejectAction = UIAlertAction.init(title: "拒绝", style: .default) { [weak self] (action) in
                self?.decisionOfAppleForLinking(isAgree: false)
            }
            alertController.addAction(agreeAction)
            alertController.addAction(rejectAction)
            present(alertController, animated: true, completion: nil)
        }
    }
    
    func decisionOfAppleForLinking(isAgree: Bool) {
        let decision = isAgree ? 1: 0
        if let currentUid = currentUid {
            let jsonDic: [String : Any] = ["account": currentUid, "role": role.rawValue(), "type": JsonType.linkResult.rawValue, "msg": "\(decision)"]
            sendPeerInfo(jsonDic: jsonDic, toPeer: applyForLinkingUser)
        }
    }
    
    func receiveResultOfApplyForLinking(result: String) {
        let isAgree = Int(result)
        if role != .owner || role != .broadcast, let isAgree = isAgree, isAgree == 1 {
            role = .broadcast
        }
    }
}

extension RoomViewController: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int) {
        currentUid = uid
        if let currentUid = currentUid {
            agoraSigKit.login(KeyCenter.appId(), account: "\(currentUid)", token: "_no_need_token", uid: 0, deviceID: nil)
        }
    }
    
    func rtcEngineConnectionDidInterrupted(_ engine: AgoraRtcEngineKit) {
         print("rtcEngineConnectionDidInterrupted")
    }
    
    func rtcEngineConnectionDidLost(_ engine: AgoraRtcEngineKit) {
        print("rtcEngineConnectionDidLost")
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOccurError errorCode: AgoraErrorCode) {
        print("didOccurError errorCode: \(errorCode)")
    }
}

extension RoomViewController: BackgroundVCDelegate {
    func backgroundVCDidPickedFinish(_ vc: BackgroundViewController, image: UIImage, index: Int) {
        if bgImageView.isHidden {
            bgImageView.isHidden = false
        }
        bgImageView.image = image
        currentBgIndex = index
        sendChannelOwnerChangedBackground()
    }
}

extension RoomViewController: BackMusicVCDelegate {
    func backMusicVCDidSelectMusic(_ vc: BackMusicViewController, filePath: String) {
        if role == .owner {
            agoraMediaKit.startAudioMixing(filePath, loopback: false, replace: false, cycle: -1)
        }
    }
    
    func backMusicVCDidStopMusic(_ vc: BackMusicViewController) {
        if role == .owner {
            agoraMediaKit.stopAudioMixing()
        }
    }
}

extension RoomViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        updateTotalUsersLabel()
        return colUserList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "userCell", for: indexPath) as! UserCell
        let user = colUserList[indexPath.item]
        cell.headImageView.image = user.headImage
        return cell;
    }
}

extension RoomViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return momentsList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "momentsCell", for: indexPath) as! MomentsCell
        let moment = momentsList[indexPath.row]
        let userIndex = totalUidIndexDic[moment.uid]
        let user = totalUserList[userIndex!]
        cell.headImageView.image = user.headImage
        cell.nameLabel.text = user.name
        cell.momentsLabel.text = moment.msg
        return cell
    }
}

extension RoomViewController: UIPopoverPresentationControllerDelegate {
    func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle {
        return .none
    }
}

extension RoomViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if let msg = textField.text {
            let jsonDic: [String : Any] = ["account": currentUid, "role": role.rawValue(), "type": JsonType.channelMsg.rawValue, "msg": msg]
            sendChannelInfo(jsonDic: jsonDic)
        }
        textField.text = ""
        self.view.endEditing(true)
        return true
    }
}
