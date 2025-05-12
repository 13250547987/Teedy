'use strict';

/**
 * 用户自助注册 – Modal 控制器
 */
angular.module('docs').controller('ModalRegister', function (
    $scope, $uibModalInstance, Restangular, $dialog
) {

    // 初始化空模型
    $scope.user = {
        username: '',
        password: '',
        email: '',
        storage_quota: 10000000000
    };

    /**
     * 提交 “创建用户” 申请
     */
    $scope.register = function () {
        // 1. 构造要提交的表单数据对象
        var formData = {
            username: $scope.user.username,
            password: $scope.user.password,
            email: $scope.user.email,
            storage_quota: $scope.user.storage_quota
        };

        // 2. 将 JS 对象序列化为 application/x-www-form-urlencoded 格式
        function toFormUrlEncoded(obj) {
            var parts = [];
            angular.forEach(obj, function (value, key) {
                parts.push(encodeURIComponent(key) + '=' + encodeURIComponent(value));
            });
            return parts.join('&');
        }

        // 3. 发送 PUT 请求到 /api/user_request
        Restangular
            .one('user_request')
            .put($scope.user)   // 直接传对象
            .then(function (resp) {
                // resp 里可能是 { status:"ok", request_id: "..." }
                $dialog.messageBox(
                    '申请提交成功',
                    '您的账户创建申请已提交，管理员审核后会通知您。',
                    [{ result: 'ok', label: '好的', cssClass: 'btn-primary' }],
                    function () {
                        $uibModalInstance.close(resp.request_id);
                    }
                );
            })
            .catch(function (e) {
                var title = '申请提交失败';
                var msg = '网络或服务器异常，请稍后再试。';
                if (e.data && e.data.type === 'AlreadyExistingUsername') {
                    msg = '该用户名已被占用，请换一个再试。';
                }
                $dialog.messageBox(
                    title,
                    msg,
                    [{ result: 'ok', label: '好的', cssClass: 'btn-primary' }]
                );
            });
    };

    /** 关闭 / 取消按钮 */
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});
