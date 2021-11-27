<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Firebase\Auth\Token\Exception\InvalidToken;

class LoginController extends Controller
{
    public $auth;

    public function __construct()
    {
        $this->auth = app('firebase.auth');
    }

    //Mengecek apakah user firebase sudah terdaftar di database?
    public function signinCheck(Request $request) {
        $data = $request->user();
        if (!is_null($data)) {
            $data_out = [
                "is_registered" => "true"
            ];
            if (!is_null($data->role)) {
                $data_out = array_merge($data_out, array("is_completed" => "true"));
                $data_out = array_merge($data_out, array("role" => $data->role));
                $data_out = array_merge($data_out, array("last_name" => $data->last_name));
            } else
                $data_out = array_merge($data_out, array("is_completed" => "false"));
        }
    }
}
