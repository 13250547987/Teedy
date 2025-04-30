'use strict';

/**
 * Settings user page controller.
 */
angular.module('docs').controller('SettingsUserController', ['$scope', '$state', '$stateParams', 'Restangular', '$dialog', '$translate',
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
      Restangular.all('user_request').getList().then(function (requests) {
        $scope.registrationRequests = requests;
      });
    };

    $scope.processRequest = function (request, action) {
      $scope.selectedRequest = request;
      $scope.selectedAction = action;
      $scope.request = {};
      $('#processRequestModal').modal('show');
    };

    $scope.confirmProcessRequest = function () {
      if ($scope.selectedAction === 'approve') {
        Restangular.one('user_request', $scope.selectedRequest.id).customPUT({
          action: 'approve',
          password: $scope.request.password
        }).then(function () {
          $scope.loadRegistrationRequests();
          $('#processRequestModal').modal('hide');
          $dialog.messageBox($translate.instant('settings.user.request_approved_title'),
            $translate.instant('settings.user.request_approved_message'),
            [{ result: 'ok', label: 'OK', cssClass: 'btn-primary' }]);
        });
      } else if ($scope.selectedAction === 'reject') {
        Restangular.one('user_request', $scope.selectedRequest.id).customPUT({
          action: 'reject'
        }).then(function () {
          $scope.loadRegistrationRequests();
          $('#processRequestModal').modal('hide');
          $dialog.messageBox($translate.instant('settings.user.request_rejected_title'),
            $translate.instant('settings.user.request_rejected_message'),
            [{ result: 'ok', label: 'OK', cssClass: 'btn-primary' }]);
        });
      }
    };

    // Initialize
    $scope.loadRegistrationRequests();
  }
]);