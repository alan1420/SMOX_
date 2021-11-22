<?php

use App\Http\Controllers\LoginController;
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

Route::post('signinCheck', [LoginController::class, 'signinCheck']);

Route::middleware('auth:api')->group(function () {
    Route::get('data', function() {
        $data = request()->user();
        $data2 = $data->first_name;
        return "You are signed in, $data2";
    });

    Route::post('signup-finalize', [UserController::class, 'storeFinal']);
});