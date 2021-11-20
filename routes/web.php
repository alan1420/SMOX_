<?php

use Illuminate\Support\Facades\Route;
use App\Classes\FirebaseToken;
use App\Models\User;

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

Route::get('/data', function () {
    $auth = app('firebase.auth');

    $users = $auth->listUsers($defaultMaxResults = 1000, $defaultBatchSize = 1000);

    //return request()->input('name');
    // $token_data = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImY1NWUyOTRlZWRjMTY3Y2Q5N2JiNWE4MTliYmY3OTA2MzZmMTIzN2UiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiUmFobWFkaWxsYWggTWF1bGFuYSIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaDFMcERDdWk2NWJ0bXVKakFSRWxSMy1oTklHSEYxdG8xVkR6VmZIQT1zOTYtYyIsInJvbGUiOiJhZG1pbiIsImlzcyI6Imh0dHBzOi8vc2VjdXJldG9rZW4uZ29vZ2xlLmNvbS9zbW94LWFwcCIsImF1ZCI6InNtb3gtYXBwIiwiYXV0aF90aW1lIjoxNjM3MDY3MjU4LCJ1c2VyX2lkIjoiaHNKeXVRaEhIVVZrN0t2ZEpkTklVbkN4MGYwMiIsInN1YiI6ImhzSnl1UWhISFVWazdLdmRKZE5JVW5DeDBmMDIiLCJpYXQiOjE2MzcwOTE4NzYsImV4cCI6MTYzNzA5NTQ3NiwiZW1haWwiOiJyYWhtYWRpbGxhaC5tYXVsYW5hMTFAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZ29vZ2xlLmNvbSI6WyIxMTUzNTM2OTgyNzUxNTE5Nzg0MjQiXSwiZW1haWwiOlsicmFobWFkaWxsYWgubWF1bGFuYTExQGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6Imdvb2dsZS5jb20ifX0.nNgNvNZjUFCtTCBRWkDxNHWCpoPoBDaJFHgq4DyjFw3j-evd65MyjbdKCGABQ5wZMqCJGNj4y8rNXbJvsqd13Zmhf_-wyLHZZ14D91VCyUbmc2htByvODKcAHPXAnx8YUweIaH9Vh8d9UdDVxeyaIKOw8erM14azR6srLgFh4VQG3YQzWcIIud4Mv7xi1z3FHzifVt2eOqiuVCJD9GdlmaMb340zM42BtAQfGje5dGztdHDb_wz30dC785_eeKEWUUKBhoTKR487JIH6Na4Xynw15oNSrfNLA9DBD2wKHlINnGOKjh-cQ2sBEF2rGAjFFUN1TiLVGwnlCAvosie4Ew";
    // $payload = (new FirebaseToken($token_data))->verify(
    //    config('services.firebase.project_id')
    // );

    // ddd($users);  

    foreach ($users as $user) {        
        echo(json_encode($user));
    }

});

Route::get('/coba', function () {
    $string = "16/08/2000";
    return str_replace('/', '-', $string);
    //return $string;
});

Route::get('/coba2', function () {
    ddd(User::where('uid', "FkXwQvivtUPb9sIt0TTvIsjz3aR2")->get());
//return $string;
});
