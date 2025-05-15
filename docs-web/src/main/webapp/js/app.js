// 添加路由
app.config(function ($routeProvider) {
    $routeProvider
        .when('/register-request', {
            templateUrl: 'template/registerRequest.html',
            controller: 'RegisterRequestCtrl',
            resolve: {
                check: function ($location, UserService) {
                    // 如果用户已登录，重定向到首页
                    if (UserService.isAuthenticated()) {
                        $location.path('/');
                    }
                }
            }
        })
        .when('/user-requests', {
            templateUrl: 'template/userRequests.html',
            controller: 'UserRequestsCtrl',
            resolve: {
                check: function ($location, UserService) {
                    // 检查用户是否已登录且具有管理员权限
                    if (!UserService.isAuthenticated() || !UserService.hasRole('ADMIN')) {
                        $location.path('/login');
                    }
                }
            }
        });
});

// 添加控制器
app.controller('RegisterRequestCtrl', function ($scope) {
    // 页面标题
    $scope.pageTitle = '申请注册账号';
});

app.controller('UserRequestsCtrl', function ($scope) {
    // 页面标题
    $scope.pageTitle = '用户注册申请管理';

    // 刷新列表
    $scope.refreshList = function () {
        // 触发列表刷新
        $('#requestList').trigger('refresh');
    };
}); 