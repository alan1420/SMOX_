<?php

namespace App\Http\Controllers;

use App\Models\PatientAssignment;
use App\Models\PatientMedicine;
use App\Models\User;
use Illuminate\Http\Request;
use Firebase\Auth\Token\Exception\InvalidToken;

class CaretakerController extends Controller
{
    public $auth;

    public function __construct()
    {
        $this->auth = app('firebase.auth');
    }

    public function assignPatient(Request $request) {
        $data_req = $request->all();
        $data_patient = User::where('username', $data_req['username'])->first();
        if (!is_null($data_patient)) {
            try {
                $data_caretaker = $request->user();
                PatientAssignment::create([
                    "caretaker_id" => $data_caretaker->id,
                    "patient_id" => $data_patient->id
                ]);
                $status = [
                    "message" => "Success!"
                ];
                return response()->json($status, 200);
            } catch(\Illuminate\Database\QueryException $e){
                $errorCode = $e->errorInfo[1];
                if($errorCode == '1062'){
                    $status = [
                        "message" => "Patient already has a caretaker!"
                    ];
                    return response()->json($status, 404); 
                } else {
                    return response('', 500);
                }
            } catch (\Throwable $e) {
                //echo 'The token could not be parsed: '.$e->getMessage();
                return response('', 500);
            }
        } else {
            $status = [
                "message" => "Patient not found!"
            ];
            return response()->json($status, 404);            
        }        
    }

    public function addPatientMedicine(Request $request) {
        $data_req = $request->all();
        try {
            PatientMedicine::create($data_req);
            $status = [
                "message" => "Success!"
            ];
            return response()->json($status, 200);
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
                    $arr_sort = array_merge($arr_sort, array("birthday" => User::where('uuid', $user->uid)->first()->birthday));
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
}
