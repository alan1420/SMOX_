<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PatientMedicine extends Model
{
    use HasFactory;
    protected $table = 'patient_medicine';
    protected $guarded = ['id'];

    public function patient(){
        return $this->belongsTo(User::class, 'patient_id');
    }

    public function patientHistory(){
        return $this->hasMany(PatientHistory::class, 'medicine_patient_id');
    }
}
