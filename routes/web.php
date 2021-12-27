<?php

use Illuminate\Support\Facades\Route;
use App\Classes\FirebaseToken;
use App\Http\Controllers\PatientController;
use App\Models\User;
use App\Models\PatientAssignment;
use App\Models\PatientHistory;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;
use Kreait\Firebase\Messaging\Notification;
use Kreait\Firebase\Messaging\CloudMessage;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Route::get('get-patient-data', [PatientController::class, 'getPatientData']);

