using System;
using Newtonsoft.Json;

namespace WPFDatingApp.ApiClasses
{
    public partial class Matches
    {
        [JsonProperty("?xml")] public Xml Xml { get; set; }

        [JsonProperty("PAGE")] public Page Page { get; set; }
    }

    public partial class Matches
    {
        public static Matches FromJson(string json)
        {
            return JsonConvert.DeserializeObject<Matches>(json, Converter.Settings);
        }
    }
    
}