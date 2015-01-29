/**
 * @module myApp
 *
 * @author Sebastian Dass&eacute;
 */
angular.module('myApp')
    /**
     * The controller for the cockpit view.
     *
     * @class cockpitCtrl
     */
    .controller('cockpitCtrl', ['$location', '$log', '$scope', 'arrayUtil', 'Invite', 'invitesPromise',
        'messagesPromise', 'Model', 'restService', 'surveysPromise', 'Status', 'Survey', 'TimePeriod',
        function($location, $log, $scope, arrayUtil, Invite, invitesPromise,
            messagesPromise, Model, restService, surveysPromise, Status, Survey, TimePeriod) {

            'use strict';

            // resolve the promises passed to this route
            $scope.surveys = Model.importMany(Survey, surveysPromise);
            $scope.invites = Model.importMany(Invite, invitesPromise);
            $scope.messages = messagesPromise;

            // preselects the first survey and invite in the list
            $scope.selectedInvite = $scope.invites[0];
            $scope.selectedSurvey = $scope.surveys[0];


            $scope.showSurveyDetails = true;

            /**
             * Switches to the survey creation view to create a new survey.
             *
             * @method addSurvey
             */
            $scope.addSurvey = function() {
                $location.path('/survey');
            };

            /**
             * Selects the specified survey.
             *
             * @method selectSurvey
             * @param  {Survey} survey the survey
             */
            $scope.selectSurvey = function(survey) {
                if (!survey) return;
                $scope.selectedSurvey = survey;
                restService.getSurveyInvites(survey.oid)
                    .then(function(invites) {
                        $scope.selectedSurvey.invites = Model.importMany(Invite, invites);
                    });
                $log.debug($scope.selectedSurvey);
            };

            /** Preselect the first survey on page load and get its invites. */
            $scope.selectSurvey($scope.surveys[0]);

            /**
             * Switches to the survey edit view to edit the selected survey.
             *
             * @method editSurvey
             */
            $scope.editSurvey = function() {
                if (!$scope.selectedSurvey) {
                    $log.log('Keine Terminumfrage ausgewaehlt.');
                    return;
                }
                $location.path('/survey/' + $scope.selectedSurvey.oid);
            };

            /**
             * Deletes the selected survey and refreshes the cockpit view.
             *
             * @method deleteSelectedSurvey
             */
            $scope.deleteSelectedSurvey = function() {
                if (!$scope.selectedSurvey) {
                    return;
                }
                $log.debug('deleting ... ')
                restService.doDelete($scope.selectedSurvey)
                    .then(function(success) {
                        arrayUtil.remove($scope.surveys, $scope.selectedSurvey);
                        $log.debug('deleted.')
                            // $location.path('/cockpit');
                    });
            };

            /**
             * Selects the specified invite.
             *
             * @method selectInvite
             * @param  {Invite} invite the invite
             */
            $scope.selectInvite = function(invite) {
                $scope.selectedInvite = invite;
                $log.debug('selectInvite(): ', $scope.selectedInvite);
            };

            /**
             * Sets the ignored status of the selected invite according to the
             * specified tribool value. Finally saves the invite on the server.
             *
             * @method setSelectedInviteIgnoredStatus
             * @param {Status} status the status of the invite
             */
            $scope.setSelectedInviteIgnoredStatus = function(status) {
                $scope.selectedInvite.setIgnored(status);
                restService.doSave($scope.selectedInvite);
            };

            var sendMessagesToParticipant = function() {
                restService.notifyParticipants($scope.selectedSurvey.oid)
                    .then(function(success) {
                        $location.path('/cockpit');
                    });
            };

            $scope.confirmSelectedSurvey = function() {
                $scope.selectedSurvey.success = Status.YES;
                restService.doSave($scope.selectedSurvey)
                    .then(function() {
                        sendMessagesToParticipant();
                    });
            };

            $scope.rejectSelectedSurvey = function() {
                $scope.selectedSurvey.success = Status.NO;
                // $scope.selectedSurvey.determinedTimePeriod = TimePeriod.NÙLL();
                restService.doSave($scope.selectedSurvey)
                    .then(function()  {
                        sendMessagesToParticipant();
                    });
            };

            $scope.toggleSurveyDetails = function() {
                $scope.showSurveyDetails = true;
            };

            $scope.toggleInviteDetails = function() {
                $scope.showSurveyDetails = false;
            };

            $scope.saveSelectedInvite = function() {
                return function(resultingTimePeriods) {
                    $scope.selectedInvite.concreteAvailability = resultingTimePeriods;
                    restService.doSave($scope.selectedInvite);
                };
            };
            // $scope.hasNoAcceptation = function() {
            //     arrayUtil.forEach($scope.selectedSurvey.invites, function(invite) {
            //         if (invite.isHost == false && invite.isIgnored != 'NO') {
            //             return true;
            //         }
            //     });
            //     return false;
            // }
        }
    ]);