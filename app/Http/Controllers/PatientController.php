<?php

namespace App\Http\Controllers;

use App\Models\PatientAssignment;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Firebase\Auth\Token\Exception\InvalidToken;
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
}