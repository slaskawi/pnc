/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

(function () {

  var module = angular.module('pnc.import', [
    'ui.router',
    'ui.bootstrap',
    'pnc.common.restclient',
    'pnc.util.date_utils',
    'angularUtils.directives.uiBreadcrumbs',
    'pnc.util.confirmClick'
  ]);


  module.config(['$stateProvider', function ($stateProvider) {


    $stateProvider.state('import', {
      abstract: true,
      url: '/import',
      views: {
        'content@': {
          templateUrl: 'common/templates/single-col.tmpl.html'
        }
      },
      data: {
        displayName: 'Product Import',
        proxy: 'import.product'
      },
      resolve: {
      }
    });


  }]);
})();
