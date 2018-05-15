//
//  UIView+ExProperty.m
//  AgoraAudio
//
//  Created by CavanSu on 07/02/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

#import "UIView+ExProperty.h"

@implementation UIView (ExProperty)
@dynamic borderColor, borderWidth, cornerRadius, masksToBounds;

- (void)setBorderColor:(UIColor *)borderColor {
    self.layer.borderColor = borderColor.CGColor;
}

- (void)setBorderWidth:(CGFloat)borderWidth {
    self.layer.borderWidth = borderWidth;
}

- (void)setCornerRadius:(CGFloat)cornerRadius {
    self.layer.cornerRadius = cornerRadius;
}

- (void)setMasksToBounds:(BOOL)masksToBounds {
    self.layer.masksToBounds = masksToBounds;
}
@end


@implementation UILabel (ExProperty)
@dynamic adjustsFontSizeToFit;

- (void)setAdjustsFontSizeToFit:(BOOL)adjustsFontSizeToFit {
    self.adjustsFontSizeToFitWidth = adjustsFontSizeToFit;
}
@end
