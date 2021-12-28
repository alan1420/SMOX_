<?php

namespace App\Http\Controllers;

use App\Models\PatientAssignment;
use App\Models\PatientMedicine;
use App\Models\User;
use Illuminate\Http\Request;
use Firebase\Auth\Token\Exception\InvalidToken;
use App\Http\Controllers\PatientController;
use Illuminate\Support\Arr;
use Kreait\Firebase\Messaging\Notification;
use Kreait\Firebase\Messaging\CloudMessage;

class CaretakerController extends Controller
{
    public $auth;
    public $messaging;

    public function __construct()
    {
        $this->auth = app('firebase.auth');
        $this->messaging = app('firebase.messaging');
    }

    public function checkUser($username) {
        $patient = User::where('username', $username)->first();
        if ($patient->patient()->first()) 
            return response()->json(["message" => "Patient already has a caretaker!"], 200);                        
        else {
            try {
                $data = collect();
                $user = $this->auth->getUser($patient->uuid);
                $data->put("patient_id", $patient->id);
                $data->put("fullname", $user->displayName);
                $data->put("birthday", $patient->birthday->format('F d, Y'));
                $data->put("email", $user->email);
                $data->put("phoneNumber", $user->phoneNumber);
                return response()->json($data, 200);              
                //return $patient_caretaker;
            } catch (\Throwable $e) {
                return response('', 500);                
            }
        }
    }

    public function assignPatient($id) {        
            try {
                $data_caretaker = request()->user();
                PatientAssignment::create([
                    "caretaker_id" => $data_caretaker->id,
                    "patient_id" => $id
                ]);
                return response()->json(["success" => "yes"], 200);
            } catch (\Throwable $e) {
                //echo 'The token could not be parsed: '.$e->getMessage();
                return response('', 500);
            }                    
    }

    public function addPatientMedicine(Request $request) {
        $data_req = $request->all();
        try {
            $patientId = $data_req['patient_id'];
            $slot = $data_req['slot'];
            unset($data_req['patient_id']);
            unset($data_req['slot']);
            $data = PatientMedicine::updateOrCreate([
                'patient_id' => $patientId,
                'slot' => $slot
            ], $data_req);

            if ($tokenfcm = User::find($patientId)->fcm_token != null) {
                //FCM Codes
                $deviceToken = $tokenfcm;
                $title = 'SMOX-app';
                $body = 'Caretaker has updated your data.';
                $notification = Notification::create($title, $body);
                // $data = [
                //     'first_key' => 'First Value',
                //     'second_key' => 'Second Value',
                // ];

                $message = CloudMessage::withTarget('token', $deviceToken)
                    ->withNotification($notification) // optional
                    //->withData($data) // optional
                ;
                $this->messaging->send($message);
            }
            
            return response()->json($data, 200);				
        } catch(\Illuminate\Database\QueryException $e){
            return response('', 500);
        } catch (\Throwable $e) {
            return response('', 500);                
        }       
    }
	
	public function deletePatientMedicine($id) {
        try {
            PatientMedicine::destroy($id);
            return response()->json(["a" => "b"], 200);				
        } catch(\Illuminate\Database\QueryException $e){
            return response('', 500);
        } catch (\Throwable $e) {
            return response('', 500);                
        }       
    }

    public function showPatient(Request $request) {
        $caretaker = $request->user();
        $out = collect();
        $list_uuid = collect();
        if (!is_null($caretaker->caretaker()->first())) {
            $patient_caretaker = $caretaker->caretaker()->get();
            //get uuid then check the data in firebase     
            foreach ($patient_caretaker as $patient) {
                $list_uuid->push(User::find($patient->patient_id)->only(['uuid'])['uuid']);
                //$out->push($patient->patient()->first()->only(['first_name', 'last_name', 'birthday']));
            }
            try {
                $users = $this->auth->getUsers($list_uuid->toArray());
                $user_result = array_values($users);
                $user_filtered = [];
                foreach ($user_result as $user) {
                    $arr_sort = [];
                    $arr_sort = array_merge($arr_sort, array("patient_id" => User::where('uuid', $user->uid)->first()->id));
                    $arr_sort = array_merge($arr_sort, array("fullname" => $user->displayName));
                    $arr_sort = array_merge($arr_sort, array("birthday" => User::where('uuid', $user->uid)->first()->birthday->format('F d, Y')));
                    $arr_sort = array_merge($arr_sort, array("email" => $user->email));
                    $arr_sort = array_merge($arr_sort, array("phoneNumber" => $user->phoneNumber));
                    //$user_filtered = array_merge($user_filtered, $arr_sort);
                    array_push($user_filtered, $arr_sort);                    
                }
                return response()->json(array("data" => $user_filtered), 200);              
                //return $patient_caretaker;
            } catch (\Throwable $e) {
                return response('', 500);                
            }
        } else {
            $out->put('empty', 'true');
            return response()->json($out, 200);
        }
        // return response()->json($out, 200);
    }

    //Get all data
    public function getCaretakerData(Request $request) {
        $all_data = collect();
        $all_data->put('role', 1);
        $caretaker = $request->user();

        $caretaker_uuid = $caretaker->uuid;

        try {
            //User data
            $user_data = collect();

            $data_firebase = $this->auth->getUser($caretaker_uuid);
            $user_data->put('name', $caretaker->last_name);
            $user_data->put('fullname', "$caretaker->first_name $caretaker->last_name");
            $user_data->put('username', $caretaker->username);
            $user_data->put('birthday', $caretaker->birthday->format('F d, Y'));
            $user_data->put('email', $data_firebase->email);
            $user_data->put('phoneNumber', $data_firebase->phoneNumber);
            $all_data->put('user_data', $user_data);
          
        } catch (\Throwable $e) {
            return response($e, 500);  
            //throw $th;
        }

        $patientController = new PatientController();

        //Get the first patient
        if ($request->patient_id == null) {
            if ($caretaker->caretaker()->first() != null) {
                $patient_id = $caretaker->caretaker()->first()['patient_id']; 
                $request->request->add(['patient_id' => $patient_id]);
            //If there's no patient
            } else {
                $all_data->put('patient_data', 'empty');
                return response()->json($all_data, 200);
            }           
        }
                
        $out = $patientController->getPatientData($request, $all_data);

        return response()->json($out, 200);
    }
}
