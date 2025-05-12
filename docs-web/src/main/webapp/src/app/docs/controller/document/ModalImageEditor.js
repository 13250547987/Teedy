// ModalImageEditor.js
angular.module('docs').controller('ModalImageEditor', function (
    $scope, $uibModalInstance, file, $timeout
) {
    $scope.file = file;
    $scope.cropper = null;

    $timeout(function () {           // 等下一轮 digest，DOM 已完成
        const img = document.getElementById('image-to-edit');
        if (!img) { console.error('找不到 <img> 元素'); return; }

        const buildCropper = () => {
            if ($scope.cropper) return;  // 防重复
            $scope.cropper = new Cropper(img, { viewMode: 1, autoCropArea: 1 });
            console.log('Cropper init OK');
        };

        img.onload = buildCropper;    // 图片加载后才初始化
        img.onerror = () => console.error('图片加载失败');

        if (img.complete) {            // 若已缓存立即执行
            buildCropper();
        }
    });

    $scope.save = function () {
        if (!$scope.cropper) { return; }

        $scope.cropper.getCroppedCanvas().toBlob(function (blob) {
            // ⬇︎ 用原名字+类型封装成 File
            const editedFile = new File(
                [blob],
                $scope.file.name,
                { type: $scope.file.mimetype, lastModified: Date.now() }
            );
            $uibModalInstance.close(editedFile);
        }, $scope.file.mimetype);
    };

    $scope.rotate = function (deg) {
        if ($scope.cropper) { $scope.cropper.rotate(deg); }
    };

    $scope.flipX = function () {
        if ($scope.cropper) {
            const scaleX = $scope.cropper.getData().scaleX || 1;
            $scope.cropper.scaleX(-scaleX);
        }
    };

    $scope.flipY = function () {
        if ($scope.cropper) {
            const scaleY = $scope.cropper.getData().scaleY || 1;
            $scope.cropper.scaleY(-scaleY);
        }
    };

    $scope.reset = function () {
        if ($scope.cropper) { $scope.cropper.reset(); }
    };



    $scope.cancel = () => $uibModalInstance.dismiss();
});
