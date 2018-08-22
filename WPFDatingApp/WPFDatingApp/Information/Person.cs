using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WPFDatingApp.Information
{
    public class Person
    {
        public string Name { get; set; }
        public int Age { get; set; } 
        public Gender Gender { get; set; }
        public string Summery { get; set; }
        public Gender Interestetin { get; set; }
    }

    public enum Gender
    {
        Boy = 0,
        Girl = 1
    }
}
