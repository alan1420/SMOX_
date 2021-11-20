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
    public function store_google($uid)
    {

    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $data = $request->all();

        $fullname = $data['first_name'] . " " . $data['last_name'];

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

        $uid = array("uid" => $createdUser->uid);
        $data = array_merge($data, $uid);

        $data['birthday'] = str_replace('/', '-', $data['birthday']);
        $data['birthday'] = date("Y-m-d", strtotime($data['birthday']));

        $user = User::create($data);

        return response()->json($user, 201);
    }

    public function storeFinal(Request $request)
    {
        $data_req = $request->all();

        $token = $request->bearerToken();

        try {
            $verifiedIdToken = $this->auth->verifyIdToken($token);
        } catch (InvalidToken $e) {
            echo 'The token is invalid: '.$e->getMessage();
        } catch (\InvalidArgumentException $e) {
            echo 'The token could not be parsed: '.$e->getMessage();
        }       
        // if you're using lcobucci/jwt ^4.0
        $uid = $verifiedIdToken->claims()->get('sub');
        $data_out = User::where('uid', $uid)
                    ->update($data_req);

        return response()->json($data_out, 200);
    }

    /**
     * Display the specified resource.
     *
     * @param  \App\Models\User  $user
     * @return \Illuminate\Http\Response
     */
    public function show(User $user)
    {
        //
    }

    public function signin(Request $request)
    {
        $token = $request->token;

        try {
            $verifiedIdToken = $this->auth->verifyIdToken($token);
        } catch (InvalidToken $e) {
            echo 'The token is invalid: '.$e->getMessage();
        } catch (\InvalidArgumentException $e) {
            echo 'The token could not be parsed: '.$e->getMessage();
        }       
        // if you're using lcobucci/jwt ^4.0
        $uid = $verifiedIdToken->claims()->get('sub');
        $data = User::where('uid', $uid)->first();
        if ($data->exists()) {
            $data_out = [
                "is_registered" => "true"
            ];
            if (!is_null($data->role)) 
                $data_out = array_merge($data_out, array("is_completed" => "true"));
            else 
                $data_out = array_merge($data_out, array("is_completed" => "false"));
            // exists
            //store_google($uid);
            //return "User didn't exist";
        } else {
            $data_out = [
                "is_registered" => "false"
            ];
            
        }
        //return $data->get();
        return response()->json($data_out, 200);        
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \App\Models\User  $user
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, User $user)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  \App\Models\User  $user
     * @return \Illuminate\Http\Response
     */
    public function destroy(User $user)
    {
        //
    }
}
