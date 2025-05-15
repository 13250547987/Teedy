'use strict';

/**
 * Settings user page controller.
 */
angular.module('docs').controller('SettingsUser', ['$scope', '$state', '$stateParams', 'Restangular', '$dialog', '$translate',
  function ($scope, $state, $stateParams, Restangular, $dialog, $translate) {
    /**
     * Load users from server.
     */
    $scope.loadUsers = function () {
      Restangular.one('user/list').get({
        sort_column: 1,
        asc: true
      }).then(function (data) {
        $scope.users = data.users;
      });
    };

    $scope.loadUsers();

    /**
     * Edit a user.
     */
    $scope.editUser = function (user) {
      $state.go('settings.user.edit', { username: user.username });
    };

    $scope.registrationRequests = [];
    $scope.selectedRequest = null;
    $scope.selectedAction = null;
    $scope.request = {};

    $scope.loadRegistrationRequests = function () {
      Restangular.one('user_request').get().then(function (data) {
        $scope.registrationRequests = data.requests;
      });
    };

    /**
     * 直接处理注册申请：Approve 时 prompt 输入密码，Reject 时 confirm 确认
     */
    $scope.processRequest = function (req, action) {
      var payload = { action: action, password: '' };
      if (action === "approve") {
        payload.password = req.password;
      } else {
        if (!confirm('确认要拒绝用户 “' + req.username + '” 的注册申请吗？')) {
          return;
        }
      }
      console.log(payload);

      // 用 jQuery 把对象变成 form-urlencoded 字符串
      var body = $.param(payload);
      // -> "action=approve&password=123456789"

      Restangular
        .one('user_request', req.id)
        .customPUT(
          body,        // 作为请求体
          '',          // sub-path 为空
          {},          // query params 为空
          {            // headers
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
          }
        )
        .then(function () {
          $scope.loadRegistrationRequests();
          $scope.loadUsers();
          $dialog.messageBox(
            action === 'approve'
              ? '注册申请已通过'
              : '注册申请已拒绝',
            action === 'approve'
              ? '该用户的注册申请已通过审批。'
              : '该用户的注册申请已被拒绝。',
            [
              {
                result: 'ok',
                label: '确定',
                cssClass: 'btn-primary'
              }
            ]
          );
        });
    }

    // $scope.confirmProcessRequest = function () {
    //   if ($scope.selectedAction === 'approve') {
    //     Restangular.one('user_request', $scope.selectedRequest.id).customPUT({
    //       action: 'approve',
    //       password: $scope.request.password
    //     }).then(function () {
    //       $scope.loadRegistrationRequests();
    //       $('#processRequestModal').modal('hide');
    //       $dialog.messageBox($translate.instant('settings.user.request_approved_title'),
    //         $translate.instant('settings.user.request_approved_message'),
    //         [{ result: 'ok', label: 'OK', cssClass: 'btn-primary' }]);
    //     });
    //   } else if ($scope.selectedAction === 'reject') {
    //     Restangular.one('user_request', $scope.selectedRequest.id).customPUT({
    //       action: 'reject'
    //     }).then(function () {
    //       $scope.loadRegistrationRequests();
    //       $('#processRequestModal').modal('hide');
    //       $dialog.messageBox($translate.instant('settings.user.request_rejected_title'),
    //         $translate.instant('settings.user.request_rejected_message'),
    //         [{ result: 'ok', label: 'OK', cssClass: 'btn-primary' }]);
    //     });
    //   }
    // };

    // Initialize
    $scope.loadRegistrationRequests();
  }
]);