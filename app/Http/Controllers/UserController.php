<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Firebase\Auth\Token\Exception\InvalidToken;

class UserController extends Controller
{
    public $auth;

    public function __construct()
    {
        $this->auth = app('firebase.auth');
    }

    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store_google($uuid)
    {

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

        $userProperties = [
            'email' => $data['email'],
            'emailVerified' => false,
            'phoneNumber' => $data['phoneNumber'],
            'password' => $data['password'],
            'displayName' => $fullname,
            'disabled' => false,
        ];

        unset($data['email']);
        unset($data['password']);
        unset($data['phoneNumber']);

        $createdUser = $this->auth->createUser($userProperties);

        $uuid = array("uuid" => $createdUser->uid);
        $data = array_merge($data, $uuid);

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
