//
//  UIView+ExProperty.h
//  AgoraAudio
//
//  Created by CavanSu on 07/02/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView (ExProperty)
@property (nonatomic) IBInspectable UIColor *borderColor;
@property (nonatomic) IBInspectable CGFloat borderWidth;
@property (nonatomic) IBInspectable CGFloat cornerRadius;
@property (nonatomic) IBInspectable BOOL masksToBounds;
@end

@interface UILabel (ExProperty)
@property (nonatomic) IBInspectable BOOL adjustsFontSizeToFit;
@end
