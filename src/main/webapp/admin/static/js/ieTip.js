(function() {
    window.isIE = window.ActiveXObject || 'ActiveXObject' in window;
    if (!window.isIE) {
        return;
    }
    window.alertConStr = '<div style="position: absolute;top: 50%;left: 50%;width: 398px;margin: -239px 0 0 -230px;padding: 10px 40px;text-align: center;background-color: #fff;border-radius: 2px;">\
        <div style="position: relative;padding: 15px 0 20px;border-bottom: 1px solid #F3F3F3;">\
            <span style="font-size: 20px;color: #000;">请升级您的浏览器</span>\
        </div>\
        <div class="con">\
            <div style="margin: 30px 0 20px 0;"><img src="//static.test.soufunimg.com/common_m/pc_public/images/bg_esfie8.png" alt="ie8头部图片" style="height:79px;"></div>\
            <p style="font-size: 14px;color: #999;line-height: 24px;">尊敬的用户，您现在使用的Internet Explore 浏览器，建议您使用其它现代浏览器（如：Google Chrome 或 Mozilla Firefox）,以便您能正常访问。</p>\
            <dl style="width: 294px;margin: 0 auto;margin-top: 25px">\
                <dd style="text-align:center;margin: 0">\
                    <div style="display:inline-block;width: 113px;height: 38px;border: 1px solid #CCCCCC;border-radius: 2px;margin-bottom: 15px;padding: 5px 0 0 25px;cursor: pointer;text-align:left;">\
                        <a href="http://www.chromeliulanqi.com/"><img src="//static.test.soufunimg.com/common_m/pc_public/images/chrome_bgesf.jpg" style="height:32px;" alt="chrome浏览器"></a>\
                    </div>\
                    <div style="display:inline-block;width: 113px;height: 38px;border: 1px solid #CCCCCC;border-radius: 2px;margin-bottom: 15px;padding: 5px 0 0 25px;cursor: pointer;text-align:left;">\
                        <a href="http://www.firefox.com.cn/"><img src="//static.test.soufunimg.com/common_m/pc_public/images/fiefox_bgesf.jpg" style="height:32px;" alt="fiefox浏览器"></a>\
                    </div>\
                </dd>\
            </dl>\
        </div>\
    </div>';

    // 外层盒子
    window.alertBox = document.createElement('div');
    window.alertBox.style.cssText += ';position: fixed;z-index: 10001;top: 0;left: 0;width: 100%;height: 100%;';

    window.alertBox.innerHTML = window.alertConStr;
    document.body.appendChild(window.alertBox);
}())