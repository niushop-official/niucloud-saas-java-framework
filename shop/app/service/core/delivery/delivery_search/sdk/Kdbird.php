<?php

namespace addon\shop\app\service\core\delivery\delivery_search\sdk;


class Kdbird
{
    private $EBusinessID; // 授权key
    private $AppKey; // 快递100分配的公司编码
    private $url;
    private $is_pay;

    public function __construct($config)
    {
        $this->EBusinessID = $config[ "kdniao_id" ];
        $this->AppKey = $config[ "kdniao_app_key" ];
        $this->url = 'https://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx';
        $this->is_pay = $config[ "kdniao_is_pay" ];
    }

    public function orderTracesSubByJson($shipper_code, $logistic_code, $mobile)
    {
        $request_array = array (
            'ShipperCode' => $shipper_code,
            'LogisticCode' => $logistic_code,
            'CustomerName' => substr($mobile, 7, 10)
        );

        $requestData = json_encode($request_array);

        $datas = array (
            'EBusinessID' => $this->EBusinessID,
            'RequestType' => '1002',
            'RequestData' => urlencode($requestData),
            'DataType' => '2',
        );

        if ($this->is_pay == 2) $datas[ 'RequestType' ] = 8001;

        $datas[ 'DataSign' ] = $this->encrypt($requestData, $this->AppKey);
        $result = $this->sendPost($this->url, $datas);
        //根据公司业务处理返回的信息......
        $result = json_decode($result, true);
        $res = [];
        if ($result[ "Success" ] == false) {
            $res[ "success" ] = false;
            $res[ "reason" ] = $result[ "Reason" ];
        } else {
            $list = [];
            if (!empty($result[ 'Traces' ])) {
                foreach ($result[ 'Traces' ] as $trace) {
                    $list[] = [
                        'datetime' => $trace[ 'AcceptTime' ],
                        'remark' => $trace[ 'AcceptStation' ]
                    ];
                }
            }
            $res = [
                'success' => $result[ 'Success' ],
                'reason' => !empty($result[ 'Reason' ]) ? $result[ 'Reason' ] : '',
                'status' => !empty($result[ 'State' ]) ? $result[ 'State' ] : '',
                'status_name' => !empty($result[ 'State' ]) ? $this->getStatusName($result[ 'State' ]) : '',
                'shipper_code' => !empty($result[ 'ShipperCode' ]) ? $result[ 'ShipperCode' ] : '',
                'logistic_code' => !empty($result[ 'LogisticCode' ]) ? $result[ 'LogisticCode' ] : '',
                'list' => $list
            ];
        }
        return $res;
    }

    /**
     * 电商Sign签名生成
     * @param data 内容
     * @param appkey Appkey
     * @return DataSign签名
     */
    public function encrypt($data, $appkey)
    {
        return urlencode(base64_encode(md5($data . $appkey)));
    }

    /**
     *  post提交数据
     * @param string $url 请求Url
     * @param array $datas 提交的数据
     * @return url响应返回的html
     */
    public function sendPost($url, $datas)
    {
        $postdata = http_build_query($datas);
        $options = array (
            'http' => array (
                'method' => 'POST',
                'header' => 'Content-type:application/x-www-form-urlencoded',
                'content' => $postdata,
                'timeout' => 15 * 60 // 超时时间（单位:s）
            )
        );
        $context = stream_context_create($options);
        $result = file_get_contents($url, false, $context);
        return $result;
    }

    /**
     * 物流跟踪状态
     * @param $state
     */
    public function getStatusName($status)
    {
        $data = [
            0 => "暂无轨迹信息",
            1 => "已揽收",
            2 => "在途中",
            3 => "已签收",
            4 => "问题件",
            5 => "转寄",
            6 => "清关"
        ];
        $status_name = isset($data[ $status ]) ? $data[ $status ] : '';
        return $status_name;
    }
}