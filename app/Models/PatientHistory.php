<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PatientHistory extends Model
{
    use HasFactory;
    protected $table = 'patient_history';
    protected $guarded = ['id'];

    public function patientMedicine(){
        return $this->belongsTo(PatientMedicine::class, 'medicine_patient_id');
    }
}
