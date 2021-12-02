<?php

use App\Http\Controllers\CaretakerController;
use App\Http\Controllers\LoginController;
use App\Http\Controllers\PatientController;
use App\Http\Controllers\UserController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::post('signup', [UserController::class, 'store']);

Route::middleware('auth:api')->group(function () {
    Route::get('signinCheck', [LoginController::class, 'signinCheck']);
    Route::post('signup-finalize', [UserController::class, 'storeFinal']);
    
    Route::post('assign-patient', [CaretakerController::class, 'assignPatient']);
    Route::get('show-patient', [CaretakerController::class, 'showPatient']);
    Route::post('add-medicine', [CaretakerController::class, 'addPatientMedicine']);
    Route::get('get-caretaker-data', [CaretakerController::class, 'getCaretakerData']);
    
    Route::post('show-history', [PatientController::class, 'showHistory']);
    Route::get('show-caretaker', [PatientController::class, 'showCaretaker']);
    Route::get('get-patient-data', [PatientController::class, 'getPatientData']);
});

