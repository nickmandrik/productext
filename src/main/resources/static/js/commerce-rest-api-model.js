function CommerceRestAPIModel() {

    var self = this;

    var urlExportAssets = '/commerce/api/exportAssets';
    var urlValidateAssets = '/commerce/api/validateAssets';
    var urlConfirmImportAssets = '/commerce/api/importAssets';

    var currentTokenUpload;

    self.RestViewModel = function() {
        var requestModel = this;


        requestModel.additionalHeaders = ko.observable('');
        requestModel.url = ko.observable('');
        requestModel.inputData = ko.observable('');
        requestModel.httpMethod = ko.observable("GET");

        requestModel.httpMethodButtons = ['GET', 'POST', 'PUT', 'DELETE'];

        requestModel.handleHttpMethodClick = function (pData, pEvent) {
            requestModel.httpMethod(pEvent.currentTarget.value);
            requestModel.toggleHttpMethodButton(pEvent.currentTarget);
        };

        requestModel.toggleHttpMethodButton = function (pSelected) {
            for (var i = 0; i < requestModel.httpMethodButtons.length; i++) {
                $("#admin-" + requestModel.httpMethodButtons[i]).removeClass("btn-success");
            }
            $(pSelected).addClass("btn-success");
        };

        requestModel.handleRequestClick = function() {
            self.executeQuery(requestModel.url(), requestModel.httpMethod(),
                requestModel.additionalHeaders(), requestModel.inputData())
            /*'/ccadmin/v1/files?folder=/thirdparty','GET'*/
        }

    };

    self.exportAssets = function() {
        $('body').addClass('loading');
        $.fileDownload(urlExportAssets, {
            successCallback: function (url) {
                $('body').removeClass('loading');
            },
            failCallback: function (html, url) {
                $('body').removeClass('loading');
            }
        })
    };

    self.importAssets = function() {
        $( '#uploaded-file' ).trigger('click');
    };

    self.validateAssets = function () {
        var file = $('#uploaded-file');
        if (!file.val()) {
            event.preventDefault();
            alert('Please choose a document!');
        } else {
            var formData = new FormData();
            formData.append('file', file.get(0).files[0]);
            validateAssetsAjax(formData);
        }

        function validateAssetsAjax(formData) {
            $.ajax({
                url: urlValidateAssets,
                data: formData,
                type: 'POST',
                contentType: false,
                processData: false,
                success: successValidateAssetsCall,
                error: function (jqXHR, textStatus, errorThrown) {
                    $('body').removeClass('loading');
                    alert(textStatus + ': ' + errorThrown);
                },
                beforeSend: function() {
                    $('body').addClass('loading');
                }
            });
        }

        function successValidateAssetsCall(data) {
            $('body').removeClass('loading');
            currentTokenUpload = data['token'];
            document.getElementById('totalNum').innerHTML = data['total'];
            document.getElementById('unchangedCountNum').innerHTML = data['unchangedCount'];
            document.getElementById('newCountNum').innerHTML = data['newCount'];
            document.getElementById('modifiedCountNum').innerHTML = data['modifiedCount'];
            document.getElementById('warningCountNum').innerHTML = data['warningCount'];
            document.getElementById('errorCountNum').innerHTML = data['errorCount'];
            $('#validateAssetsModal').modal('show');
        }

    };

    self.getFileParam = function() {
        try {
            self.validateAssets();
            /*if (file) {
                var fileSize = 0;

                if (file.size > 1024 * 1024) {
                    fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
                }else {
                    fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';
                }

                document.getElementById('uploaded-file-name').innerHTML = 'Name: ' + file.name;
                document.getElementById('uploaded-file-size').innerHTML = 'Size: ' + fileSize;
            }*/
        }catch(e) {
            alert('error');
           /* var file1 = document.getElementById('uploaded-file1').value;
            file1 = file1.replace(/\\/g, '/').split('/').pop();
            document.getElementById('uploaded-file-name').innerHTML = 'Name: ' + file1;*/
        }
    };

    self.cancelImport = function() {
        if (confirm('Are you want to cancel import?')) {
            $('#validateAssetsModal').modal('toggle');
        }
    };

    self.submitImport = function() {
        if (confirm('Are you want to submit import?')) {
            var data = {'token': currentTokenUpload};
            $.ajax({
                url: urlConfirmImportAssets,
                data: JSON.stringify(data),
                type: 'POST',
                contentType: 'application/json',
                dataTpe: 'application/json',
                success: successImportAssetsCall,
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(textStatus + ': ' + errorThrown);
                },
                beforeSend: function() {
                    $('#validateAssetsModal').modal('toggle');
                }
            });
        }


        function successImportAssetsCall(data) {
            document.getElementById('totalStat').innerHTML = data['total'];
            document.getElementById('unchangedCountStat').innerHTML = data['unchangedCount'];
            document.getElementById('newSuccessCountStat').innerHTML = data['newSuccessCount'];
            document.getElementById('modifiedSuccessCountStat').innerHTML = data['modifiedSuccessCount'];
            document.getElementById('newErrorCountStat').innerHTML = data['newErrorCount'];
            document.getElementById('modifiedErrorCountStat').innerHTML = data['modifiedErrorCount'];
            $('#statusImportAssetsModal').modal('show');
        }

    };


    self.executeQuery = function(query, type, additionalHeaders, inputData) {
        var data = {query: query, type: type, addHeaders: additionalHeaders, inputData: inputData};
        $.ajax({
            url: '/commerce/api/executeQuery',
            data: JSON.stringify(data),
            type: 'POST',
            contentType: 'application/json',
            processData: false,
            success: function () {
                $('body').removeClass('loading');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $('body').removeClass('loading');
            },
            beforeSend: function () {
                $('body').addClass('loading');
            }

        });
    }
}

var commerceRestAPIModel = new CommerceRestAPIModel();
var restViewModel = new commerceRestAPIModel.RestViewModel();
ko.applyBindings(restViewModel);