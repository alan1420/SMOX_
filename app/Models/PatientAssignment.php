<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PatientAssignment extends Model
{
    use HasFactory;
    protected $table = 'patient_assignment';
    protected $guarded = ['id'];

    public function caretaker(){
        return $this->belongsTo(User::class, 'caretaker_id');
    }

    public function patient(){
        return $this->belongsTo(User::class, 'patient_id');
    }

    public function patientMedicine(){
        return $this->hasMany(PatientMedicine::class, 'patient_id', 'id');
    }
}
