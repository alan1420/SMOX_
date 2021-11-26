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
            $token = $request->bearerToken();
            try {
                $verifiedIdToken = $this->auth->verifyIdToken($token);
    
                $uuid = $verifiedIdToken->claims()->get('sub');
                //$uuid = "uaIPsRute7OrFQBDZL2OUGQSgnM2";
                $data_caretaker = User::where('uuid', $uuid)->first();
                PatientAssignment::create([
                    "caretaker_id" => $data_caretaker->id,
                    "patient_id" => $data_patient->id
                ]);
                $status = [
                    "message" => "Success!"
                ];
                return response()->json($status, 200);
            } catch (InvalidToken $e) {
                //echo 'The token is invalid: '.$e->getMessage();
                return response('', 500);
            } catch (\InvalidArgumentException $e) {
                //echo 'The token could not be parsed: '.$e->getMessage();
                return response('', 500);
            } catch(\Illuminate\Database\QueryException $e){
                $errorCode = $e->errorInfo[1];
                if($errorCode == '1062'){
                    $status = [
                        "message" => "Patient already has a caretaker!"
                    ];
                    return response()->json($status, 404); 
                }
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
        } catch (InvalidToken $e) {
            //echo 'The token is invalid: '.$e->getMessage();
            return response('', 500);
        } catch (\InvalidArgumentException $e) {
            //echo 'The token could not be parsed: '.$e->getMessage();
            return response('', 500);
        } catch(\Illuminate\Database\QueryException $e){
            return response('', 500);
        }        
    }
}
