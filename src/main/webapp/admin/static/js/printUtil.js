/* ========================打印功能相关=========================*/
    function convertToChineseDes(num) {
        let strOutput = '';
        let strUnit = '仟佰拾亿仟佰拾万仟佰拾元角分';
        num += '00';
        const intPos = num.indexOf('.');
        if (intPos >= 0) { num = num.substring(0, intPos) + num.substr(intPos + 1, 2); }
        strUnit = strUnit.substr(strUnit.length - num.length);
        for (let i = 0; i < num.length; i++) { strOutput += '零壹贰叁肆伍陆柒捌玖'.substr(num.substr(i, 1), 1) + strUnit.substr(i, 1); }
        return strOutput.replace(/零角零分$/, '整').replace(/零[仟佰拾]/g, '零').replace(/零{2,}/g, '零').replace(/零([亿|万])/g, '$1').replace(/零+元/, '元').replace(/亿零{0,3}万/, '亿').replace(/^元/, '零元');
    }
    function formatDateTime(timeStamp) {
        const date = new Date();
        date.setTime(timeStamp);
        const y = date.getFullYear();
        let m = date.getMonth() + 1;
        m = m < 10 ? '0' + m : m;
        let d = date.getDate();
        d = d < 10 ? '0' + d : d;
        let h = date.getHours();
        h = h < 10 ? '0' + h : h;
        let minute = date.getMinutes();
        let second = date.getSeconds();
        minute = minute < 10 ? '0' + minute : minute;
        second = second < 10 ? '0' + second : second;
        return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
    }
    const gridDate = function(value) {
        if (!value) {
            return '未支付或者是货到付款';
        }
        const appointDeliveryTime = new Date(value);
        return appointDeliveryTime.customFormat('#YYYY#/#MM#/#DD# #AMPM##hh#:#mm#');
    };
    // 打印订单
    function getPrintHtmlArray(data) {
        const printHtmlArray = new Array();
        const cityId = getCookie('cityId');
        if (data) {
            let assignedSalesman = '';
            if (data.salesmanName) {
                assignedSalesman = data.salesmanName;
            } else if (window.$ && $('#assignedName').val() && $('#assignedName').val() != undefined) {
			    assignedSalesman = $('#assignedName').val();
            }
            const payOrOrderTime = '支付时间：' + gridDate(data.payTime);

			// 支付方式
            let way = data.payMethod;
            if (way == 'yue') {
                way = '余额支付';
            } else if (way == 'cod') {
                way = '货到付款';
            } else if (way == 'weixin') {
                way = '微信支付';
            } else if (way == 'ali') {
                way = '支付宝支付';
            }

			// 订单来源
            const orderSourceFlag = data.orderSource;
            let orderSource = '';
            if (orderSourceFlag == '0') {
                orderSource = 'APP下单';
            } else if (orderSourceFlag == '1') {
                orderSource = '销售员下单' + (data.placeOrderMan ? ':' + data.placeOrderMan : '');
            } else if (orderSourceFlag == '2') {
                orderSource = '后台下单' + (data.placeOrderMan ? ':' + data.placeOrderMan : '');
            } else if (orderSourceFlag == '3') {
                orderSource = '小程序下单' + (data.placeOrderMan ? ':' + data.placeOrderMan : '');
            } else if (!orderSourceFlag) {
                orderSource = '';
            }

            let remark = data.remark;
            if (!remark) {
                remark = '';
            }
            let appointDeliveryStartTimeStr = data.appointDeliveryStartTimeStr;
            if (!appointDeliveryStartTimeStr) {
                appointDeliveryStartTimeStr = '';
            }
            let appointDeliveryEndTimeStr = data.appointDeliveryEndTimeStr;
            if (!appointDeliveryEndTimeStr) {
                appointDeliveryEndTimeStr = '';
            }
            let consigneeMerchantName = data.consigneeMerchantName;
            if (!consigneeMerchantName) {
            	consigneeMerchantName = '';
            } else {
            	consigneeMerchantName = consigneeMerchantName + '|';
            }
            let lineName = data.lineName;
            if (!lineName) {
            	lineName = '未分配线路';
            }
            const orderType = data.type;
            let companyName = '		<span style=\'width:90%\'>公司全称：' + data.companyObj[cityId].name;
            if (cityId === '5' && orderType !== '2') {
                companyName = '';
            }
            let titleCompanyName = data.companyObj[cityId].nameAbbr;
            if (cityId === '5' && orderType !== '2') {
                titleCompanyName = '冻品云';
            }
            let string1 = '';
            let title = '';
            if (!data.addressDistrictName || data.addressDistrictName === 'null') {
                data.addressDistrictName = '';
            }
            if (!data.addressStreetName || data.addressStreetName === 'null') {
                data.addressStreetName = '';
            }
            if (orderType !== '1') {
                string1 = '        <td>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>商户名称:' + consigneeMerchantName + lineName + '<font/><b/></div>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>收件人:' + data.consigneeName + '<font/><b/></div>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>收件电话：' + data.consigneeTelephone + '<font/><b/></div>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>地址:' + data.addressDistrictName + '，' + data.addressStreetName + '，' + data.consigneeAddress + '<font/><b/></div>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>预约:' + appointDeliveryStartTimeStr + ' 至  ' + appointDeliveryEndTimeStr + '<font/><b/></div>' +
                        '</td>';
                title = ' 销售单</div>'
            } else {
                string1 = '        <td>';
                if (data.defaultMerchantName && data.defaultMerchantName !== 'null') {
                    string1 += '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>商户名称:' + data.defaultMerchantName + '<font/><b/></div>'
                }
                string1 += '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>自提时间:' + dpy.formatTime(new Date(data.appointDeliveryEndTime), true) + '<font/><b/></div>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>取货地址:' + data.addressDistrictName + '，' + data.addressStreetName + '，' + data.consigneeAddress + '<font/><b/></div>' +
                    '</td>';
                title = ' 自提销售单</div>'
            }

            const resultHeader =
                '<div id=\'content\' style=\'width:98%;height:98%\'>' +
                '<div style=\'text-align:center;font-size:20px\'>' + titleCompanyName + title +
                '<table style=\'width:95%;margin:auto;text-align:left;font-size:14px;\' border=\'0\'>' +
                '    <tr>' +
                '        <td style=\'width: 45%\'>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'>订单号:' + data.orderNo + '|' + data.assignNo + '<br/>订单来源:' + orderSource + '</div>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><font size=\'3\'>支付方式:' + way +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><font size=\'3\'>支付时间:<font/><br/>' + gridDate(data.payTime) + '</div>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'>注册账户:' + data.merchantTelephone + '</div>' +
                '        </td>' +
                string1 +
                '    </tr>' +
                '    <tr>' +
                '        <td colspan=\'2\'>' +
                '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>订单备注:' + remark + '<font/><b/></div>' +
                '        </td>' +
                '    </tr>' +
                '</table>' +
                '<table style=\'width: 95%;margin: auto;text-align: center; border-collapse: collapse; border: #000 2px solid;\'>' +
                '<tr><th style="border: #000 2px solid;">序号</th>' +
                '<th style="border: #000 2px solid;">仓位</th>' +
                '<th style="border: #000 2px solid;">商品名称</th>' +
                '<th style="border: #000 2px solid;">规格</th>' +
                '<th style="border: #000 2px solid;">单位</th>' +
                '<th style="border: #000 2px solid;">数量</th>' +
                '<th style="border: #000 2px solid;">单价/元</th>' +
                '<th style="border: #000 2px solid;">金额/元</th>';

            const resultBodyArray = [];
            let totalNum = 0;
            const shippingPrice = data.shippingPrice;
            const productList = data.orderProductSnapshotList;
            const len = productList.length;
            for (var i = 0; i < len; i++) {
			// 实际下单数量
                if (data.orderStatus != 0) {
                    let purNum = 0, refNum = 0, unitPrice = 0;
                    if (productList[i].purchasedNum) {
                        purNum = productList[i].purchasedNum;
                    }
                    if (productList[i].refundNum) {
                        refNum = productList[i].refundNum;
                    }
                    if (productList[i].specificationUnitPrice) {
                        unitPrice = productList[i].specificationUnitPrice;
                    }
                    totalNum = totalNum + (purNum - refNum);
                }
            }
			// 固定： 每页10条
            const size = 10;
			// 总 pageNum 页
            const pageNum = parseInt((len - 1) / size) + 1;
			// 总共打印 pageNum 页
            for (let int = 0; int < pageNum; int++) {
                resultBodyArray[int] = '';
				// 固定： 每页10条
				// 从第几页第几个开始打印
                for (var i = int * size; i < ((int + 1) === pageNum ? len : (int + 1) * size); i++) {
					// 实际下单数量
                    let purchasedNum = 0, refundNum = 0;
                    if (productList[i].purchasedNum) {
                        purchasedNum = productList[i].purchasedNum;
                    }
                    if (productList[i].refundNum) {
                        refundNum = productList[i].refundNum;
                    }
                    let realOrderNum = 0;
                    if (data.orderStatus != 0) {
                        realOrderNum = (parseFloat(purchasedNum) - parseFloat(refundNum)).toFixed(2);
                    }
                    resultBodyArray[int] +=
						'<tr>' +
						'<td style="border: #000 2px solid;">' + (i + 1) + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].warehouseLocationMaster + '</td>';
                    if (productList[i].activityCode != null && productList[i].activityCode != '') {
                        resultBodyArray[int] += '<td style="border: #000 2px solid;">' + '（特价）' + productList[i].productName + '</td>';
                    } else {
                        resultBodyArray[int] += '<td style="border: #000 2px solid;">' + productList[i].productName + '</td>';
                    }
                    resultBodyArray[int] +=
						'<td style="border: #000 2px solid;">' + productList[i].specificationName + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].unit + '</td>' +
						'<td style="border: #000 2px solid;">' + dpy.precisionMath(realOrderNum) + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].specificationUnitPrice + '</td>' +
                        '<td style="border: #000 2px solid;">' + (productList[i].specificationUnitPrice * purchasedNum).toFixed(2) + '</td>' +
						'</tr>';
                }
                let payablePrice = data.payablePrice;
                if (!payablePrice) {
                    payablePrice = 0.00;
                }

                let pointDeductionPrice = data.pointDeductionPrice;
                if (!pointDeductionPrice) {
                    pointDeductionPrice = 0.00;
                }

                let redPacketAmount = data.redPacketAmount;
                if (!redPacketAmount) {
                    redPacketAmount = 0.00;
                }

				// 计算优惠金额
                pointDeductionPrice = parseFloat(pointDeductionPrice) + parseFloat(redPacketAmount);
                if (orderSourceFlag == '2') {
                    payablePrice = Math.round(payablePrice);
                }
                if (way == 'cod' || way == '货到付款') {
                    payablePrice = dpy.precisionMath(payablePrice, 0);
                }
                const payablePriceDes = convertToChineseDes(payablePrice);
				// 只在最后一页打印总价,只打一次
                let resultTail = '';
                if ((int + 1) === pageNum) {
                    resultBodyArray[int] +=
                        '<tr><td colspan=\'5\' style="border: #000 2px solid;">' + payablePriceDes + '</td>' +
                        '<td style="border: #000 2px solid;">' + dpy.precisionMath(totalNum) + '</td>' +
                        '<td style="border: #000 2px solid;">应付金额</td>' +
                        '<td colspan=\'2\' style="border: #000 2px solid;">' + payablePrice + '元</td></tr>';

                    resultTail =
						'<div style=\'width: 95%;margin: auto; font-size:14px;line-height:18px; text-align: right; clear: both;\'>' +
						'    <div style=\'width: 30%;margin: auto; font-size:15px;line-height:18px; text-align: left; float: left\'><b>总数量:</b>' + dpy.precisionMath(totalNum) + '</div>' +
						'    <div style=\'width: 70%;margin: auto; font-size:14px;line-height:18px; text-align: right; float: right\'><b>总金额:</b>' + data.productPrice + '元   <b>运费:</b>' + shippingPrice + '元    <b>优惠金额:</b>' + pointDeductionPrice + '元　<b>应付金额:</b>' + payablePrice + '元</div>' +
						'</div>' +
						'<div style=\'width: 95%;margin: auto; font-size:12px; clear: both;\'>' +
						'	 <span style=\'width:100%\'>温馨提示：超过24小时若无质量问题不受理退货</span>' +
						'</div>' +
						'<div style=\'width: 95%;margin: auto; font-size:12px; clear: both;\'>' +
						companyName +　'  地址：' + data.companyObj[cityId].address + '　电话：' + data.companyObj[cityId].tel + ' </span>' +
						'</div>';
                }
                const resultPage =
					'<div style=\'width: 95%;margin: auto; font-size:16px;line-height:18px; text-align: right; clear: both;\'>' +
					'    <div style=\'width: 70%;margin: auto; font-size:16px;line-height:18px; text-align: right; float: right\'><b>第' + (int + 1) + '/' + pageNum + '页</b></div>' +
					'</div>';
                const result = resultHeader + resultBodyArray[int] + '</table>' + resultTail + resultPage + '</div>';
				// var result = resultHeader + resultBodyArray[int] +"</table></div>";
                printHtmlArray.push(result);
            }
        }
        return printHtmlArray;
    }

// 打印包含二维码的销售单
    function printSalesList(data) {
        const printHtmlArray = new Array();
        const cityId = getCookie('cityId');
        if (data) {
            let assignedSalesman = '';
            if (data.salesmanName) {
                assignedSalesman = data.salesmanName;
            } else if (window.$ && $('#assignedName').val() && $('#assignedName').val() != undefined) {
                assignedSalesman = $('#assignedName').val();
            }
            let payOrOrderTime = '支付时间：' + gridDate(data.payTime);

        // 支付方式
            let way = data.payMethod;
            if (way == 'yue') {
                way = '余额';
            } else if (way == 'cod') {
                way = '货到付款!!';
                payOrOrderTime = '下单时间：' + gridDate(data.orderTime);
            } else if (way == 'weixin') {
                way = '微信';
            } else if (way == 'ali') {
                way = '支付宝';
            }

        // 订单来源
            const orderSourceFlag = data.orderSource;
            let orderSource = '';
            if (orderSourceFlag == '0') {
                orderSource = 'APP下单';
            } else if (orderSourceFlag == '1') {
                orderSource = '销售员下单';
            } else if (orderSourceFlag == '2') {
                orderSource = '后台下单';
            } else if (!orderSourceFlag) {
                orderSource = '';
            }

            let remark = data.remark;
            if (!remark) {
                remark = '';
            }
            let appointDeliveryStartTimeStr = data.appointDeliveryStartTimeStr;
            if (!appointDeliveryStartTimeStr) {
                appointDeliveryStartTimeStr = '';
            }
            let appointDeliveryEndTimeStr = data.appointDeliveryEndTimeStr;
            if (!appointDeliveryEndTimeStr) {
                appointDeliveryEndTimeStr = '';
            }
            let consigneeMerchantName = data.consigneeMerchantName;
            if (!consigneeMerchantName) {
                consigneeMerchantName = '';
            } else {
                consigneeMerchantName = consigneeMerchantName + '|';
            }
            let lineName = data.lineName;
            if (!lineName) {
                lineName = '未分配线路';
            }

            let title = '';
            if (data.title === '销售单') {
                title = '<div style=\'float:left;font-size:20px;margin-left: 20px\'>' + data.title + '</div>';
            } else {
                title = '<div style=\'float:right;font-size:20px;margin-right: 100px\'>' + data.title + '</div>';
            }

            const resultHeader =
            '<div style="width:110px;height:110px;position:absolute;right:5px;top:0;"><img src="data:image/png;base64,' + data.orderNoQrcode + '" border="0"/></div>'
+
        '<div id=\'content\' style=\'width:98%;height:98%\'>' +
        '<div>' +
        title +
        '<div style=\'text-align:center;font-size:20px\'>' + data.companyObj[cityId].nameAbbr + '</div>' +
        ' </div>' +
            '<table style=\'width:95%;margin:auto;text-align:left;font-size:14px;\' border=\'0\'>' +
            '    <tr>' +
            '        <td style=\'width: 45%\'>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>订单号:' + data.orderNo + '|' + data.assignNo + '<font/><b/><br/><b><font size=\'3\'>订单来源:' + orderSource + '<font/><b/></div>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>支付方式:' + way +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>支付时间:<font/><br/>' + gridDate(data.payTime) + '<b></div>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>注册账户:' + data.merchantTelephone + '<b><font size=\'3\'></div>' +
            '        </td>' +
            '        <td>' +
            '            <div style=\'width: 340px;align=left;line-height:18px;\'><b><font size=\'3\'>商户名称:' + consigneeMerchantName + lineName + '<font/><b/></div>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>收件人:' + data.consigneeName + '<font/><b/></div>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>收件电话：' + data.consigneeTelephone + '<font/><b/></div>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>地址:' + data.addressDistrictName + '，' + data.addressStreetName + '，' + data.consigneeAddress + '<font/><b/></div>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>预约:' + appointDeliveryStartTimeStr + ' 至  ' + appointDeliveryEndTimeStr + '<font/><b/></div>' +
            '</td>' +
            '    </tr>' +
            '    <tr>' +
            '        <td colspan=\'2\'>' +
            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>订单备注:' + remark + '<font/><b/></div>' +
            '        </td>' +
            '    </tr>' +
            '</table>' +
            '<table style=\'width: 95%;margin: auto;text-align: center; border-collapse: collapse; border: #000 2px solid;\'>' +
            '<tr><th style="border: #000 2px solid;">序号</th>' +
            '<th style="border: #000 2px solid;">仓位</th>' +
            '<th style="border: #000 2px solid;">商品名称</th>' +
            '<th style="border: #000 2px solid;">规格</th>' +
            '<th style="border: #000 2px solid;">单位</th>' +
            '<th style="border: #000 2px solid;">数量</th>' +
            '<th style="border: #000 2px solid;">单价/元</th>' +
            '<th style="border: #000 2px solid;">金额/元</th>';

            const resultBodyArray = [];
            let totalNum = 0;
            const shippingPrice = data.shippingPrice;
            const productList = data.orderProductSnapshotList;
            const len = productList.length;
            for (var i = 0; i < len; i++) {
            // 实际下单数量
                if (data.orderStatus != 0) {
                    let purNum = 0, refNum = 0, unitPrice = 0;
                    if (productList[i].purchasedNum) {
                        purNum = productList[i].purchasedNum;
                    }
                    if (productList[i].refundNum) {
                        refNum = productList[i].refundNum;
                    }
                    if (productList[i].specificationUnitPrice) {
                        unitPrice = productList[i].specificationUnitPrice;
                    }
                    totalNum = totalNum + (purNum - refNum);
                }
            }
        // 固定： 每页10条
            const size = 10;
        // 总 pageNum 页
            const pageNum = parseInt((len - 1) / size) + 1;
        // 总共打印 pageNum 页
            for (let int = 0; int < pageNum; int++) {
                resultBodyArray[int] = '';
            // 固定： 每页10条
            // 从第几页第几个开始打印
                for (var i = int * size; i < ((int + 1) === pageNum ? len : (int + 1) * size); i++) {
                // 实际下单数量
                    let purchasedNum = 0, refundNum = 0;
                    if (productList[i].purchasedNum) {
                        purchasedNum = productList[i].purchasedNum;
                    }
                    if (productList[i].refundNum) {
                        refundNum = productList[i].refundNum;
                    }
                    let realOrderNum = 0;
                    if (data.orderStatus != 0) {
                        realOrderNum = (parseFloat(purchasedNum) - parseFloat(refundNum)).toFixed(2);
                    }
                    resultBodyArray[int] +=
                    '<tr>' +
                    '<td style="border: #000 2px solid;">' + (i + 1) + '</td>' +
                    '<td style="border: #000 2px solid;">' + productList[i].warehouseLocationMaster + '</td>';
                    if (productList[i].activityCode != null && productList[i].activityCode != '') {
                        resultBodyArray[int] += '<td style="border: #000 2px solid;">' + '（特价）' + productList[i].productName + '</td>';
                    } else {
                        resultBodyArray[int] += '<td style="border: #000 2px solid;">' + productList[i].productName + '</td>';
                    }
                    resultBodyArray[int] +=
                    '<td style="border: #000 2px solid;">' + productList[i].specificationName + '</td>' +
                    '<td style="border: #000 2px solid;">' + productList[i].unit + '</td>' +
                    '<td style="border: #000 2px solid;">' + dpy.precisionMath(realOrderNum) + '</td>' +
                    '<td style="border: #000 2px solid;">' + productList[i].specificationUnitPrice + '</td>' +
                    '<td style="border: #000 2px solid;">' + (productList[i].specificationUnitPrice * purchasedNum).toFixed(2) + '</td>' +
                    '</tr>';
                }
                let payablePrice = data.payablePrice;
                if (!payablePrice) {
                    payablePrice = 0.00;
                }

                let pointDeductionPrice = data.pointDeductionPrice;
                if (!pointDeductionPrice) {
                    pointDeductionPrice = 0.00;
                }

                let redPacketAmount = data.redPacketAmount;
                if (!redPacketAmount) {
                    redPacketAmount = 0.00;
                }

            // 计算优惠金额
                pointDeductionPrice = parseFloat(pointDeductionPrice) + parseFloat(redPacketAmount);
                const payablePriceDes = convertToChineseDes(payablePrice);
                if (orderSourceFlag == '2') {
                    payablePrice = Math.round(payablePrice);
                }
            // 只在最后一页打印总价,只打一次
                let resultTail = '';
                if ((int + 1) === pageNum) {
                    resultBodyArray[int] +=
                    '<tr><td colspan=\'5\' style="border: #000 2px solid;">' + payablePriceDes + '</td>' +
                    '<td style="border: #000 2px solid;">' + dpy.precisionMath(totalNum) + '</td>' +
                    '<td style="border: #000 2px solid;">应付金额</td>' +
                    '<td colspan=\'2\' style="border: #000 2px solid;">' + payablePrice + '元</td></tr>';

                    resultTail =
                    '<div style=\'width: 95%;margin: auto; font-size:14px;line-height:18px; text-align: right; clear: both;\'>' +
                    '    <div style=\'width: 30%;margin: auto; font-size:15px;line-height:18px; text-align: left; float: left\'><b>总数量:</b>' + dpy.precisionMath(totalNum) + '</div>' +
                    '    <div style=\'width: 70%;margin: auto; font-size:14px;line-height:18px; text-align: right; float: right\'><b>总金额:</b>' + data.productPrice + '元   <b>运费:</b>' + shippingPrice + '元    <b>优惠金额:</b>' + pointDeductionPrice + '元　<b>应付金额:</b>' + payablePrice + '元</div>' +
                    '</div>' +
                    '<div style=\'width: 95%;margin: auto; font-size:12px; clear: both;\'>' +
                    '	 <span style=\'width:100%\'>温馨提示：超过24小时若无质量问题不受理退货</span>' +
                    '</div>' +
                    '<div style=\'width: 95%;margin: auto; font-size:12px; clear: both;\'>' +
                    '		<span style=\'width:90%\'>公司全称：' + data.companyObj[cityId].name +　'地址：' + data.companyObj[cityId].address + '　电话：' + data.companyObj[cityId].tel + ' </span>' +
                    '</div>';
                }
                const resultPage =
                '<div style=\'width: 95%;margin: auto; font-size:16px;line-height:18px; text-align: right; clear: both;\'>' +
                '    <div style=\'width: 70%;margin: auto; font-size:16px;line-height:18px; text-align: right; float: right\'><b>第' + (int + 1) + '/' + pageNum + '页</b></div>' +
                '</div>';
                const result = resultHeader + resultBodyArray[int] + '</table>' + resultTail + resultPage + '</div>';
            // var result = resultHeader + resultBodyArray[int] +"</table></div>";
                printHtmlArray.push(result);
            }
        }
        return printHtmlArray;
    }

    // 打印退款单
    function getPrintRefundHtmlArray(data) {
        const printHtmlArray = [];
        if (data) {
			// 支付方式
            let way = data.payMethod;
            if (way === 'yue') {
                way = '余额';
            } else if (way === 'cod') {
                way = '货到付款!!';
            } else if (way === 'weixin') {
                way = '微信';
            } else if (way === 'ali') {
                way = '支付宝';
            }

            if (!data.addressDistrictName || data.addressDistrictName === 'null') {
                data.addressDistrictName = '';
            }
            if (!data.addressStreetName || data.addressStreetName === 'null') {
                data.addressStreetName = ' ';
            }

            // 因为consigneeMerchantName 这个字段页面展示需要，这里用来做自提订单状态
            // consigneeName同上（为自提时间）
            let changeStr = '';
            if (data.consigneeMerchantName === '自提订单') {
                changeStr = '<td>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>注册账户:' + data.merchantTelephone + '<font/><b/></div>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>自提时间：' + dpy.formatTime(new Date(data.selfDeliveryTime), true) + '<font/><b/></div>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>取货地址:' + data.addressDistrictName + '-' + data.addressStreetName + '-' + data.consigneeAddress + '<font/><b/></div>' +
                    '</td>';
            } else {
                changeStr = '<td>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>注册账户:' + data.merchantTelephone + '<font/><b/></div>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>商户名称:' + data.consigneeMerchantName + '<font/><b/></div>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>收件人:' + data.consigneeName + '<font/><b/></div>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>收件电话：' + data.consigneeTelephone + '<font/><b/></div>' +
                    '            <div style=\'width: 100%;margin: auto;line-height:18px;\'><b><font size=\'3\'>地址:' + data.addressDistrictName + '-' + data.addressStreetName + '-' + data.consigneeAddress + '<font/><b/></div>' +
                    '</td>';
            }

            const resultHeader =
	            '<div id=\'content\' style=\'width:98%;height:98%\'>' +
	            '<div style=\'text-align:center;font-size:20px\'>退货申请单</div>' +
	            '<table style=\'width:95%;margin:auto;text-align:left;font-size:14px;\' border=\'0\'>' +
	            '    <tr>' +
	            '        <td style=\'width: 45%\'>' +
	            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'>退款订单号:' + data.refundOrderNo + '</div>' +
	            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'>订单号:' + data.orderNo + '</div>' +
	            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'>支付方式:' + way + '</div>' +
	            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'>申请时间:' + formatDateTime(data.timestamp) + '</div>' +
	            '            <div style=\'width: 100%;margin: auto;line-height:18px;\'>退货原因:' + data.returnCause + '</div>' +
	            '        </td>'
                         + changeStr +
	            '    </tr>' +
	            '</table>' +
	            '<table style=\'width: 95%;margin: auto;text-align: center; border-collapse: collapse; border: #000 2px solid;\'>' +
	            '<tr><th style="border: #000 2px solid;">序号</th>' +
	            '<th style="border: #000 2px solid;">商品全名</th>' +
	            '<th style="border: #000 2px solid;">规格</th>' +
	            '<th style="border: #000 2px solid;">单位</th>' +
	            '<th style="border: #000 2px solid;">数量</th>' +
	            '<th style="border: #000 2px solid;">单价</th>' +
	            '<th style="border: #000 2px solid;">金额</th></tr>';

            const resultBodyArray = [];
            let totalRefundProductPrice = 0;
            let totalRefundNum = 0;
            var returnShippingPrice = data.returnShippingPrice;
            if (!returnShippingPrice) {
                returnShippingPrice = '';
            }
            var poundageAmount = data.poundageAmount;
            if (!poundageAmount) {
                poundageAmount = '';
            }
            const productList = data.refundOrderProductSnapshotList;
            const len = productList.length;
            for (var i = 0; i < len; i++) {
                totalRefundProductPrice = totalRefundProductPrice + productList[i].refundNum * productList[i].specificationUnitPrice;
                totalRefundNum = totalRefundNum + productList[i].refundNum;
            }
            totalRefundProductPrice = dpy.precisionMath(totalRefundProductPrice);
			// 固定： 每页10条
            const size = 10;
			// 总 pageNum 页
            const pageNum = parseInt((len - 1) / size) + 1;
			// 总共打印 pageNum 页
            for (let int = 0; int < pageNum; int++) {
                resultBodyArray[int] = '';
				// 固定： 每页10条
				// 从第几页第几个开始打印
                for (var i = int * size; i < ((int + 1) === pageNum ? len : (int + 1) * size); i++) {
                    resultBodyArray[int] +=
						'<tr>' +
						'<td style="border: #000 2px solid;">' + (i + 1) + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].productName + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].specificationName + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].unit + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].refundNum + '</td>' +
						'<td style="border: #000 2px solid;">' + productList[i].specificationUnitPrice + '元</td>' +
						'<td style="border: #000 2px solid;">' + dpy.precisionMath(productList[i].specificationUnitPrice * productList[i].refundNum) + '元</td>' +
						'</tr>';
                }
                let amount = data.amount;
                if (!amount) {
                    amount = 0.00;
                }
                var returnShippingPrice = data.returnShippingPrice;
                if (!returnShippingPrice) {
                    returnShippingPrice = 0.00;
                }
                var poundageAmount = data.poundageAmount;
                if (!poundageAmount) {
                    poundageAmount = 0.00;
                }
                const	amountDes = convertToChineseDes(amount);

				// 只在最后一页打印总价,只打一次
                let resultTail = '';
                if ((int + 1) === pageNum) {
                    resultBodyArray[int] +=
						'<tr><td colspan=\'4\' style="border: #000 2px solid;">' + amountDes + '</td>' +
						'<td style="border: #000 2px solid;">' + totalRefundNum + '</td>' +
						'<td style="border: #000 2px solid;">退款金额</td>' +
						'<td style="border: #000 2px solid;">' + amount + '元</td></tr>';

                    resultTail =
						'<div style=\'width: 95%;margin: auto; font-size:14px;line-height:18px; text-align: right; clear: both;\'>' +
						'    <div style=\'width: 30%;margin: auto; font-size:14px;line-height:18px; text-align: left; float: left\'><b>总数量:</b>' + totalRefundNum + '</div>' +
						'    <div style=\'width: 70%;margin: auto; font-size:14px;line-height:18px; text-align: right; float: right\'><b>商品总金额:</b>' + totalRefundProductPrice + '元   <b>退运费:</b>' + returnShippingPrice + '元    <b>手续费:</b>' + poundageAmount + '元　<b>退款金额:</b>' + amount + '元</div>' +
						'</div>';
                }
                const resultPage =
					'<div style=\'width: 95%;margin: auto; font-size:16px;line-height:18px; text-align: right; clear: both;\'>' +
					'    <div style=\'width: 70%;margin: auto; font-size:16px;line-height:18px; text-align: right; float: right\'><b>第' + (int + 1) + '/' + pageNum + '页</b></div>' +
					'</div>' +
					'<table style=\'width:95%;margin:auto;text-align:left;font-size:14px;\' border=\'0\'>' +
					'    <tr>' +
					'        <td>销售部:</td><td>配送部:</td><td>仓储部:</td><td>财务部:</td>' +
					'    </tr>' +
					'</table>';
                const result = resultHeader + resultBodyArray[int] + '</table>' + resultTail + resultPage + '</div>';
                printHtmlArray.push(result);
            }
        }
        return printHtmlArray;
    }

    function getPrintUnassigenedArr(data) {
    	const len = data.unassignedOrderInfoVOList.length;
        const printHtmlArray = [];
        const resultHeader = '<div id=\'content\' style=\'width:98%;height:98%\'>' +
            '<div style=\'text-align:left;font-size:20px;padding-left:25px\'><b>出货订单列表：</b></div>' +
            '<div style=\'text-align:left;padding-left:25px\'>' + data.assignNos + '</div>' +
            '<div style=\'text-align:left;padding-left:25px\'>打印时间：' + data.unassignedOrderInfoVOList[0].printDate + '</div>' +
			'<div style=\'text-align:left;padding-left:25px\'>路线：' + data.lineName + '</div>' +
            '<div style=\'text-align:left;padding-left:25px\'>出货商品列表：</div>' +
            '<table style=\'width: 95%;margin: auto;text-align: left; border-collapse: collapse; border: #000 2px solid;\'>' +
            '<tr><th style="border: #000 2px solid;">序号</th>' +
            '<th style="border: #000 2px solid;">主仓位</th>' +
            '<th style="border: #000 2px solid;">商品名称</th>' +
            '<th style="border: #000 2px solid;">规格名称</th>' +
            '<th style="border: #000 2px solid;">单位</th>' +
            '<th style="border: #000 2px solid;">数量</th>' +
            '<th style="border: #000 2px solid;">备仓位</th>' +
			'<th style="border: #000 2px solid;">特价商品数</th></tr>';
        const resultBodyArray = [];
        // 固定： 每页20条
        const size = parseInt(len);
        // 总 pageNum 页
        const pageNum = parseInt((len - 1) / size) + 1;
        // 总共打印 pageNum 页
        for (let int = 0; int < pageNum; int++) {
            resultBodyArray[int] = '';
            // 从第几页第几个开始打印
            for (let i = int * size; i < ((int + 1) === pageNum ? len : (int + 1) * size); i++) {
                resultBodyArray[int] +=
                    '<tr>' +
                    '<td style="border: #000 2px solid;">' + (i + 1) + '</td>' +
                    '<td style="border: #000 2px solid;">' + data.unassignedOrderInfoVOList[i].warehouseLocationMaster + '</td>' +
                    '<td style="border: #000 2px solid;">' + data.unassignedOrderInfoVOList[i].productName + '</td>' +
                    '<td style="border: #000 2px solid;">' + data.unassignedOrderInfoVOList[i].specificationName + '</td>' +
                    '<td style="border: #000 2px solid;">' + data.unassignedOrderInfoVOList[i].unit + '</td>' +
                    '<td style="border: #000 2px solid;">' + data.unassignedOrderInfoVOList[i].outQuantity + '</td>' +
                    '<td style="border: #000 2px solid;">' + data.unassignedOrderInfoVOList[i].warehouseLocationSlave + '</td>' +
					'<td style="border: #000 2px solid;">' + data.unassignedOrderInfoVOList[i].activityProudctQuantity + '</td>' +
                    '</tr>';
            }
            const resultTail =
                '<div style=\'width: 95%;margin: auto; font-size:14px;line-height:18px; text-align: right; clear: both;\'>' +
                '    <div style=\'width: 30%;margin: auto; font-size:15px;line-height:18px; text-align: right; float: right\'><b>合计:</b>' + sumUnitQuantiy(data.unassignedOrderInfoVOList) + '</div>' +
                '</div>';
            printHtmlArray.push(resultHeader + resultBodyArray[int] + '</table>' + resultTail + '</div>');
        }
        return printHtmlArray;
    }



    function sumUnitQuantiy(dataArr) {
    	if (dataArr === null || dataArr === undefined || dataArr.length === 0) {
    		return '0';
    }
        const unitQuantiyObj = {};
        for (let q = 0; q < dataArr.length; q++) {
            const data = dataArr[q];
            if (q === 0) {
                unitQuantiyObj[data.unit] = data.outQuantity;
            }
            if (q != 0) {
                if (unitQuantiyObj.hasOwnProperty(data.unit)) {
                    unitQuantiyObj[data.unit] = data.outQuantity + unitQuantiyObj[data.unit];
                } else {
                    unitQuantiyObj[data.unit] = data.outQuantity;
                }
            }
        }
        let totalOutQuantiyStr = '';
        for (const key in unitQuantiyObj) {
            if (unitQuantiyObj.hasOwnProperty(key)) {
                totalOutQuantiyStr += dpy.precisionMath(unitQuantiyObj[key]) + key + '、';
            }
        }
        totalOutQuantiyStr = totalOutQuantiyStr.substring(0, totalOutQuantiyStr.length - 1);
        return totalOutQuantiyStr;
    }

    function getPrintOutWarehousingArr(data) {
        console.log(data);
        console.log(data);
        const len = data.purchaseProductList.length;
        const purchaseProductList = data.purchaseProductList;
        let totalNum = 0;
        let totalPrice = 0;
        const printHtmlArray = [];
        for (let k = 0; k < len; k++) {
            totalNum += purchaseProductList[k].num;
            totalPrice += purchaseProductList[k].num * purchaseProductList[k].cost;
        }
        const resultHeader =
            '<div id="content"style="width:98%;height:98%">' +
            '<div>' +
            '<div style="text-align:center;font-size:20px"><b>入库单</b></div>' +
            ' </div>' +
            '<table style="width:95%;margin:auto;text-align:left;font-size:14px;" border="0">' +
            '    <tr>' +
            '        <td>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">入库仓库:' + data.warehouseName +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">运费标准(元/吨):<font/>' + data.shippingPrice + '<b></div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">供应商:' + data.supplier + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">供应商手机号:' + data.supplierTel + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">供应商地址:' + data.supplierAddress + '</div>' +
            '        </td>' +
            '        <td>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">采购支付方式:' + data.payMethod + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">其他金额:' + data.otherPrice + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">总吨数:' + data.totalTonnage + '</div>' +
            '		</td>' +
            '    </tr>' +
            '	 <tr>' +
            '        <td colspan="2">' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">入库详情:<font/><b/></div>' +
            '        </td>' +
            '    </tr>' +
            '</table>' +
            '<table style="width: 95%;margin: auto;text-align: center; border-collapse: collapse; border: #000 2px solid;">' +
            '<tr><th style="border: #000 2px solid;">序号</th>' +
            '<th style="border: #000 2px solid;">商品名称</th>' +
            '<th style="border: #000 2px solid;">规格名称</th>' +
            '<th style="border: #000 2px solid;">单位</th>' +
            '<th style="border: #000 2px solid;">入库数量</th>' +
            '<th style="border: #000 2px solid;">主仓位</th>' +
            '<th style="border: #000 2px solid;">备仓位</th>' +
            '<th style="border: #000 2px solid;">备注</th>';

        const resultBodyArray = [];

        // 固定： 每页10条
        const size = len + 1;
        // 总 pageNum 页
        const pageNum = parseInt((len - 1) / size) + 1;
        // 总共打印 pageNum 页
        for (let int = 0; int < pageNum; int++) {
            resultBodyArray[int] = '';
            // 固定： 每页10条
            // 从第几页第几个开始打印
            for (let i = int * size; i < ((int + 1) === pageNum ? len : (int + 1) * size); i++) {
                resultBodyArray[int] +=
                    '<tr>' +
                    '<td style="border: #000 2px solid;">' + (i + 1) + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].productName + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].specificationName + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].specificationUnit + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].num + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].warehouseLocationMaster + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].warehouseLocationSlave + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].comment + '</td>' +
                    '</tr>';
            }

            // 只在最后一页打印总价,只打一次
            const resultTail = '';
            if ((int + 1) === pageNum) {
                resultBodyArray[int] +=
                    '<tr><td colspan="4" style="border: #000 2px solid;">合计</td>' +
                    '<td style="border: #000 2px solid;">' + totalNum + '</td>' +
                    '<td colspan="2" style="border: #000 2px solid;"></td>' +
                    '<td></td>' +
                    '</tr>';
                const result = resultHeader + resultBodyArray[int] + '</table>' + resultTail + '</div>';
                printHtmlArray.push(result);
            }
        }
        return printHtmlArray;
    }

    /**
     * 打印入库单
     * @param data
     */
    function getPrintWarehousingArr(data) {
        console.log(data);
        const len = data.purchaseProductList.length;
        const purchaseProductList = data.purchaseProductList;
        let totalNum = 0;
        let totalPrice = 0;
        const printHtmlArray = [];
        for (let k = 0; k < len; k++) {
            totalNum += purchaseProductList[k].num;
            totalPrice += purchaseProductList[k].num * purchaseProductList[k].cost;
        }
        const resultHeader =
            '<div id="content"style="width:98%;height:98%">' +
            '<div>' +
            '<div style="text-align:center;font-size:20px"><b>入库单</b></div>' +
            ' </div>' +
            '<table style="width:95%;margin:auto;text-align:left;font-size:14px;" border="0">' +
            '    <tr>' +
            '        <td>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">入库仓库:' + data.warehouseName +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">运费标准(元/吨):<font/>' + data.shippingPrice + '<b></div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">供应商:' + data.supplier + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">供应商手机号:' + data.supplierTel + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">供应商地址:' + data.supplierAddress + '</div>' +
            '        </td>' +
            '        <td>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">采购支付方式:' + data.payMethod + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">其他金额:' + data.otherPrice + '</div>' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">总吨数:' + data.totalTonnage + '</div>' +
            '		</td>' +
            '    </tr>' +
            '	 <tr>' +
            '        <td colspan="2">' +
            '            <div style="width: 100%;margin: auto;line-height:18px;">' +
            '				<b><font size="3">入库详情:<font/><b/></div>' +
            '        </td>' +
            '    </tr>' +
            '</table>' +
            '<table style="width: 95%;margin: auto;text-align: center; border-collapse: collapse; border: #000 2px solid;">' +
            '<tr><th style="border: #000 2px solid;">序号</th>' +
            '<th style="border: #000 2px solid;">商品名称</th>' +
            '<th style="border: #000 2px solid;">规格名称</th>' +
            '<th style="border: #000 2px solid;">单位</th>' +
            '<th style="border: #000 2px solid;">入库数量</th>' +
            '<th style="border: #000 2px solid;">采购单价</th>' +
            '<th style="border: #000 2px solid;">最近一次采购单价</th>' +
            '<th style="border: #000 2px solid;">进货成本价(采购单价+运费+平摊其他费用)</th>' +
            '<th style="border: #000 2px solid;">总金额(元)</th>' +
            '<th style="border: #000 2px solid;">备注</th>';

        const resultBodyArray = [];

        // 固定： 每页10条
        const size = len + 1;
        // 总 pageNum 页
        const pageNum = parseInt((len - 1) / size) + 1;
        // 总共打印 pageNum 页
        for (let int = 0; int < pageNum; int++) {
            resultBodyArray[int] = '';
            // 固定： 每页10条
            // 从第几页第几个开始打印
            for (let i = int * size; i < ((int + 1) === pageNum ? len : (int + 1) * size); i++) {
                resultBodyArray[int] +=
                    '<tr>' +
                    '<td style="border: #000 2px solid;">' + (i + 1) + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].productName + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].specificationName + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].specificationUnit + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].num + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].purchasePrice + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].lastStockPrice + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].cost + '</td>' +
                    '<td style="border: #000 2px solid;">' + (parseFloat(purchaseProductList[i].cost) * parseFloat(purchaseProductList[i].num)).toFixed(2) + '</td>' +
                    '<td style="border: #000 2px solid;">' + purchaseProductList[i].comment + '</td>' +
                    '</tr>';
            }

            // 只在最后一页打印总价,只打一次
            const resultTail = '';
            if ((int + 1) === pageNum) {
                resultBodyArray[int] +=
                    '<tr><td colspan="4" style="border: #000 2px solid;">合计</td>' +
                    '<td style="border: #000 2px solid;">' + totalNum + '</td>' +
                    '<td colspan="3" style="border: #000 2px solid;"></td>' +
                    '<td style="border: #000 2px solid;">' + dpy.precisionMath(totalPrice) + '</td>' +
                    '<td></td>' +
                    '</tr>';
                const result = resultHeader + resultBodyArray[int] + '</table>' + resultTail + '</div>';
                printHtmlArray.push(result);
            }
        }
        return printHtmlArray;
    }
