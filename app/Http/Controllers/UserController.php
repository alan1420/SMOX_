<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Arr;
use Firebase\Auth\Token\Exception\InvalidToken;

class UserController extends Controller
{
    public $auth;

    public function __construct()
    {
        $this->auth = app('firebase.auth');
    }

    /**
     * Store a newly created resource in storage.
     * Register untuk user baru
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $data = $request->all();
        
        
        $fullname = $data['first_name'] . " " . $data['last_name'];
        
        $phoneUtil = \libphonenumber\PhoneNumberUtil::getInstance();
        $numberID = $phoneUtil->parse($data['phoneNumber'], "ID");
        $data['phoneNumber'] = $phoneUtil->format($numberID, \libphonenumber\PhoneNumberFormat::E164);

        $userProperties = [];
        //User sign-up via Google
        if (Arr::exists($data, 'uuid')) {
            $userProperties = [
                'phoneNumber' => $data['phoneNumber'],
                'displayName' => $fullname,
            ];
            $this->auth->updateUser($data['uuid'], $userProperties);
        //Sign-up via Email
        } else {
            $userProperties = [
                'email' => $data['email'],
                'emailVerified' => false,
                'phoneNumber' => $data['phoneNumber'],
                'password' => $data['password'],
                'displayName' => $fullname,
                'disabled' => false,
            ];
            $createdUser = $this->auth->createUser($userProperties);
            $data = Arr::prepend($data, $createdUser->uid, 'uuid');
        }
        Arr::pull($data, 'email');
        Arr::pull($data, 'password');
        Arr::pull($data, 'phoneNumber');        

        $data['birthday'] = str_replace('/', '-', $data['birthday']);
        $data['birthday'] = date("Y-m-d", strtotime($data['birthday']));

        $user = User::create($data);

        return response()->json($user, 201);
    }

    //Registrasi final untuk role dan username (khusus pasien)
    public function storeFinal(Request $request)
    {
        $data_req = $request->all();
        $data = $request->user();
        $data->update($data_req);
        $status = [
            "name" => $data->last_name
        ];
        return response()->json($status, 200);     
    }
}
