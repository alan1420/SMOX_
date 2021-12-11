<?php

namespace App\Http\Controllers;

use App\Models\PatientAssignment;
use App\Models\PatientMedicine;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class PatientController extends Controller
{
    public $auth;

    public function __construct()
    {
        $this->auth = app('firebase.auth');
    }

    public function showHistory(Request $request) {
        $data_req = $request->all();

        $patient = User::find($data_req['patient_id']);
        //return $patient;
        if (!is_null($patient->patientMedicine()->first())) { 
            $history_slot1 = $patient->patientMedicine()->where('slot', '1')
                            ->first()->patientHistory()
                            ->select(DB::raw('DATE(created_at) as date'))
                            ->get();
            $history_slot2 = $patient->patientMedicine()->where('slot', '2')
                            ->first()->patientHistory()
                            ->select(DB::raw('DATE(created_at) as date'))
                            ->get();
            $out_data = [];
            if ($history_slot1->isNotEmpty()) {
                $out_data = array_merge($out_data, array("history1" => 
                            array_column($history_slot1->toArray(), 'date')));
            }
            if ($history_slot2->isNotEmpty()) {
                $out_data = array_merge($out_data, array("history2" => 
                            array_column($history_slot2->toArray(), 'date')));
            }  
            return response()->json($out_data, 200);
        }      
    }

    public function showCaretaker(Request $request) {
        $patient = $request->user();
        if (!is_null($patient->patient()->first())) {
            $caretaker = $patient->patient()->first()->caretaker()->first();
            try {
                $data_firebase = $this->auth->getUser($caretaker->uuid);
                $detail_caretaker  = collect();
                $detail_caretaker->put('caretaker_id', $caretaker->id);
                $detail_caretaker->put('fullname', $data_firebase->displayName);
                $detail_caretaker->put('birthday', $caretaker->birthday->format('F d, Y'));
                $detail_caretaker->put('email', $data_firebase->email);
                $detail_caretaker->put('phoneNumber', $data_firebase->phoneNumber);
                return response()->json($detail_caretaker, 200); 
            } catch (\Throwable $e) {
                return response('', 500);  
                //throw $th;
            }
        }
    }

    public function getPatientData(Request $request, $data_incoming = null) {
        $all_data = collect();

        $patient_id = $request->patient_id;
    
        if ($patient_id == null) {
            $all_data->put('role', 2);
            $patient = $request->user();
            if (!is_null($patient->patient()->first())) {
                $caretaker = $patient->patient()->first()->caretaker()->first();
                $caretaker_uuid = $caretaker->uuid;
            } else {
                $caretaker = null;
                $caretaker_uuid = 'null';
            }
            
        } else {
            $patient = User::find($patient_id);
            $caretaker = null;
            $caretaker_uuid = 'null';
        }     
        
        try {
            //User data
            $user_data = collect();

            $patient_uuid = $patient->uuid;
            $data_firebase = $this->auth->getUsers([$patient_uuid, $caretaker_uuid]);
            $fb_user = $data_firebase[$patient_uuid];
			$user_data->put('id', $patient->id);
            $user_data->put('fullname', "$patient->first_name $patient->last_name");
            $user_data->put('username', $patient->username);
            $user_data->put('name', $patient->last_name);
            $user_data->put('birthday', $patient->birthday->format('F d, Y'));
            $user_data->put('email', $fb_user->email);
            $user_data->put('phoneNumber', $fb_user->phoneNumber);
            $all_data->put("user_data", $user_data);

            if ($data_firebase[$caretaker_uuid] != null) {
                //Caretaker data
                $caretaker_data = collect();
                $fb_care = $data_firebase[$caretaker_uuid];
                $caretaker_data->put('fullname', $fb_care->displayName);
                $caretaker_data->put('username', $caretaker->username);
                $caretaker_data->put('birthday', $caretaker->birthday->format('F d, Y'));
                $caretaker_data->put('email', $fb_care->email);
                $caretaker_data->put('phoneNumber', $fb_care->phoneNumber);
                $all_data->put("caretaker_data", $caretaker_data);
            }            
        } catch (\Throwable $e) {
            return response('', 500);  
            //throw $th;
        }

        //Medicine slot 1 and 2
        if (!is_null($patient->patientMedicine()->first())) {
            $all_data->put("medicine_list", $patient->patientMedicine()->get());

            $history_slot1 = $patient->patientMedicine()->where('slot', '1')
                            ->first();
            $history_slot2 = $patient->patientMedicine()->where('slot', '2')
                            ->first();
            $out_data = [];
			if ($history_slot1 || $history_slot2) {
				if ($history_slot1) {
					$out_data = array_merge($out_data, array("history1" => 
								array_column($history_slot1->patientHistory()
								->select(DB::raw('DATE(created_at) as date'))
								->get()->toArray(), 'date')));
				}
				if ($history_slot2) {
					$out_data = array_merge($out_data, array("history2" => 
								array_column($history_slot2->patientHistory()
								->select(DB::raw('DATE(created_at) as date'))
								->get()->toArray(), 'date')));
				}
				$all_data->put("medicine_history", $out_data);
			}       
        }

        if ($patient_id == null) {
            return response()->json($all_data, 200); 
        } else {
            $all_data->prepend($all_data['user_data'], 'patient_data');
            $all_data->forget('user_data');
            if ($data_incoming != null) {
                $data_incoming = $data_incoming->merge($all_data);
                //$all_data = $all_data->merge($data_incoming);
                return $data_incoming;
            }
            return response()->json($all_data, 200);        
        }
    }
}
